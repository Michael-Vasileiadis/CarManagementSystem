package pu.fmi.car_management_system.infrastructure.services.maintentance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pu.fmi.car_management_system.domain.repository.car.Car;
import pu.fmi.car_management_system.domain.repository.car.CarRepository;
import pu.fmi.car_management_system.domain.repository.garage.Garage;
import pu.fmi.car_management_system.domain.repository.garage.GarageRepository;
import pu.fmi.car_management_system.domain.repository.maintenance.Maintenance;
import pu.fmi.car_management_system.domain.repository.maintenance.MaintenanceRepository;
import pu.fmi.car_management_system.exception.CarNotFoundException;
import pu.fmi.car_management_system.exception.GarageNotFoundException;
import pu.fmi.car_management_system.exception.MaintenanceNotFoundException;
import pu.fmi.car_management_system.exception.ValidationException;
import pu.fmi.car_management_system.model.maintenance.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 * Implementation of all maintenance service methods needed for the maintenance controller
 */
@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    private final CarRepository carRepository;

    private final GarageRepository garageRepository;

    public List<MaintenanceResponseModel> readAll(Integer carId, Integer garageId, LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.findByGarageIdAndCarId(garageId, carId).stream()
                .filter(maintenance -> isScheduledDateInRange(maintenance.getScheduledDate(), startDate, endDate))
                .map(this::mapToMaintenanceResponseModel)
                .toList();
    }

    public MaintenanceResponseModel readById(Integer id) {
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new MaintenanceNotFoundException(id));
        return mapToMaintenanceResponseModel(maintenance);
    }

    public List<MonthlyRequestsReportModel> getMonthlyRequestsReport(Integer garageId, YearMonth startMonth, YearMonth endMonth) {
        if (!garageRepository.existsById(garageId)) {
            throw new ValidationException("A garage with the following id doesn't exist: " + garageId);
        }
        Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate = maintenanceRepository.findByGarageId(garageId).stream()
                .filter(maintenance -> isScheduledDateInRange(maintenance.getScheduledDate(), startMonth, endMonth))
                .collect(groupingBy(maintenance -> YearMonth.from(maintenance.getScheduledDate())));
        return produceMonthlyRequestsReport(maintenancesGroupedByScheduledDate, startMonth, endMonth);
    }

    @Transactional
    public MaintenanceResponseModel create(CreateMaintenanceModel createMaintenanceModel) {
        Garage garage = garageRepository.findById(createMaintenanceModel.garageId()).orElseThrow(IllegalArgumentException::new);
        Car car = carRepository.findById(createMaintenanceModel.carId()).orElseThrow(IllegalArgumentException::new);
        verifyGarageCapacity(garage, createMaintenanceModel.scheduledDate());
        verifyCarRegistrationToGarage(car, garage);
        Maintenance maintenance = Maintenance.builder()
                .car(car)
                .garage(garage)
                .serviceType(createMaintenanceModel.serviceType())
                .scheduledDate(createMaintenanceModel.scheduledDate())
                .build();
        maintenanceRepository.save(maintenance);
        return mapToMaintenanceResponseModel(maintenance);
    }

    @Transactional
    public MaintenanceResponseModel update(Integer id, UpdateMaintenanceModel updateMaintenanceModel) {
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new MaintenanceNotFoundException(id));
        Garage garage = updateMaintenanceModel.garageId() != null ?
                garageRepository.findById(updateMaintenanceModel.garageId()).orElseThrow(() ->
                        new GarageNotFoundException(updateMaintenanceModel.garageId())) : maintenance.getGarage();
        Car car = updateMaintenanceModel.carId() != null ?
                carRepository.findById(updateMaintenanceModel.carId()).orElseThrow(() ->
                        new CarNotFoundException(updateMaintenanceModel.carId())) : maintenance.getCar();
        LocalDate scheduledDate = updateMaintenanceModel.scheduledDate() != null ?
                updateMaintenanceModel.scheduledDate() : maintenance.getScheduledDate();
        String serviceType = updateMaintenanceModel.serviceType() != null ?
                updateMaintenanceModel.serviceType() : maintenance.getServiceType();
        verifyGarageCapacity(garage, scheduledDate);
        verifyCarRegistrationToGarage(car, garage);
        updateMaintenanceEntity(maintenance, garage, car, scheduledDate, serviceType);
        return mapToMaintenanceResponseModel(maintenance);
    }

    @Transactional
    public boolean delete(Integer id) {
        try {
            Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new MaintenanceNotFoundException(id));
            maintenance.setGarage(null);
            maintenance.setCar(null);
            maintenanceRepository.delete(maintenance);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean isScheduledDateInRange(LocalDate scheduledDate, LocalDate startDate, LocalDate endDate) {
        boolean isAfterStartDate = true;
        if (startDate != null) {
            isAfterStartDate = scheduledDate.isAfter(startDate);
        }
        boolean isBeforeEndDate = true;
        if (endDate != null) {
            isBeforeEndDate = scheduledDate.isBefore(endDate);
        }
        return isAfterStartDate && isBeforeEndDate;
    }

    private MaintenanceResponseModel mapToMaintenanceResponseModel(Maintenance maintenance) {
        return new MaintenanceResponseModel(maintenance.getId(),
                maintenance.getCar().getId(),
                createCarName(maintenance.getCar()),
                maintenance.getServiceType(),
                maintenance.getScheduledDate(),
                maintenance.getGarage().getId(),
                maintenance.getGarage().getName());
    }

    private String createCarName(Car car) {
        return car.getMake() + car.getModel() + car.getLicensePlate();
    }

    private void updateMaintenanceEntity(Maintenance maintenance, Garage garage, Car car, LocalDate scheduledDate, String serviceType) {
        maintenance.setGarage(garage);
        maintenance.setCar(car);
        maintenance.setScheduledDate(scheduledDate);
        maintenance.setServiceType(serviceType);
    }

    private void verifyGarageCapacity(Garage garage, LocalDate scheduledDate) {
        List<Maintenance> maintenances = maintenanceRepository.findByGarageIdAndScheduledDate(garage.getId(), scheduledDate);
        if (garage.getCapacity() <= maintenances.size()) {
            throw new ValidationException("A garage with id: " + garage.getId() +
                    " doesn't have capacity for the following date: " + scheduledDate);
        }
    }

    private void verifyCarRegistrationToGarage(Car car, Garage garage) {
        if (!garage.getCars().contains(car)) {
            throw new ValidationException("A car with id:" + car.getId() +
                    " is not registered in the garage with id: " + garage.getCars());
        }
    }

    private boolean isScheduledDateInRange(LocalDate scheduledDate, YearMonth startMonth, YearMonth endMonth) {
        YearMonth scheduledDateInYearMonthFormat = YearMonth.from(scheduledDate);
        return (startMonth.isBefore(scheduledDateInYearMonthFormat) || startMonth.equals(scheduledDateInYearMonthFormat)) &&
                (endMonth.isAfter(scheduledDateInYearMonthFormat) || endMonth.equals(scheduledDateInYearMonthFormat));
    }

    private List<MonthlyRequestsReportModel> produceMonthlyRequestsReport(Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate, YearMonth startMonth, YearMonth endMonth) {
        List<MonthlyRequestsReportModel> result = new ArrayList<>();
        YearMonth currentMonth = startMonth;
        while (currentMonth.isBefore(endMonth) || currentMonth.equals(endMonth)) {
            if (maintenanceForGivenYearMonthExist(maintenancesGroupedByScheduledDate, currentMonth)) {
                result.add(createReportWithRequests(maintenancesGroupedByScheduledDate, currentMonth));
            } else {
                result.add(createReportWithoutRequests(currentMonth));
            }
            currentMonth = currentMonth.plusMonths(1);
        }
        return result;
    }

    private boolean maintenanceForGivenYearMonthExist(Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate, YearMonth yearMonth) {
        return maintenancesGroupedByScheduledDate.containsKey(yearMonth);
    }

    private MonthlyRequestsReportModel createReportWithRequests(Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate, YearMonth currentMonth) {
        List<Maintenance> maintenancesForCurrentMonth = maintenancesGroupedByScheduledDate.get(currentMonth);
        Integer numberOfRequests = maintenancesForCurrentMonth.size();
        YearMonthModel yearMonthModel = mapToYearMonthModel(currentMonth);
        return new MonthlyRequestsReportModel(yearMonthModel, numberOfRequests);
    }

    private MonthlyRequestsReportModel createReportWithoutRequests(YearMonth currentMonth) {
        YearMonthModel yearMonthModel = mapToYearMonthModel(currentMonth);
        int numberOfRequests = 0;
        return new MonthlyRequestsReportModel(yearMonthModel, numberOfRequests);
    }

    private YearMonthModel mapToYearMonthModel(YearMonth yearMonth) {
        Integer year = yearMonth.getYear();
        String month = yearMonth.getMonth().toString();
        Boolean isLeapYear = yearMonth.isLeapYear();
        Integer monthValue = yearMonth.getMonthValue();
        return new YearMonthModel(year, month, isLeapYear, monthValue);
    }
}
