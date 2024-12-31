package pu.fmi.car_management_system.infrastructure.services.garage;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pu.fmi.car_management_system.domain.repository.car.Car;
import pu.fmi.car_management_system.domain.repository.garage.Garage;
import pu.fmi.car_management_system.domain.repository.garage.GarageRepository;
import pu.fmi.car_management_system.domain.repository.maintenance.Maintenance;
import pu.fmi.car_management_system.domain.repository.maintenance.MaintenanceRepository;
import pu.fmi.car_management_system.exception.GarageNotFoundException;
import pu.fmi.car_management_system.model.garage.CreateGarageModel;
import pu.fmi.car_management_system.model.garage.DailyAvailabilityReportModel;
import pu.fmi.car_management_system.model.garage.GarageResponseModel;
import pu.fmi.car_management_system.model.garage.UpdateGarageModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * Implementation of all garage service methods needed for the garage controller
 */
@Service
@RequiredArgsConstructor
public class GarageServiceImpl implements GarageService {

    private final GarageRepository garageRepository;

    private final MaintenanceRepository maintenanceRepository;

    public List<GarageResponseModel> readAll(@Nullable String city) {
        List<Garage> garages;
        if (isNull(city)) {
            garages = garageRepository.findAll();
        } else {
            garages = garageRepository.findByCity(city);
        }
        return garages.stream()
                .map(garage ->
                        new GarageResponseModel(garage.getId(), garage.getName(),
                        garage.getLocation(), garage.getCity(), garage.getCapacity()))
                .toList();
    }

    public GarageResponseModel readById(Integer id) {
        Garage garage = garageRepository.findById(id).orElseThrow(() -> new GarageNotFoundException(id));
        return new GarageResponseModel(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    @Transactional
    public List<DailyAvailabilityReportModel> getDailyAvailabilityReport(Integer garageId, LocalDate startDate, LocalDate endDate) {
        Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));
        Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate =
                maintenanceRepository.findByGarageIdAndScheduledDateBetween(garageId, startDate, endDate).stream()
                .collect(Collectors.groupingBy(Maintenance::getScheduledDate));
        return produceDailyAvailabilityReportInRange(maintenancesGroupedByScheduledDate, startDate, endDate, garage);
    }


    @Transactional
    public GarageResponseModel create(CreateGarageModel createGarageModel) {
        Garage garage = Garage.builder()
                .name(createGarageModel.name())
                .location(createGarageModel.location())
                .city(createGarageModel.city())
                .capacity(createGarageModel.capacity())
                .build();
        garageRepository.save(garage);
        return new GarageResponseModel(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    @Transactional
    public GarageResponseModel update(Integer id, UpdateGarageModel updateGarageModel) {
        Garage garage = garageRepository.findById(id).orElseThrow(() -> new GarageNotFoundException(id));
        updateStringFields(garage, updateGarageModel);
        if (updateGarageModel.capacity() != null) {
            garage.setCapacity(updateGarageModel.capacity());
        }
        return new GarageResponseModel(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    @Transactional
    public boolean delete(Integer id) {
        Garage garage = garageRepository.findById(id).orElseThrow(() -> new GarageNotFoundException(id));
        try {
            for (Car car : garage.getCars()) {
                car.getGarages().remove(garage);
            }
            garageRepository.delete(garage);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private void updateStringFields(Garage garage, UpdateGarageModel garageModel) {
        if (garageModel.name() != null) {
            garage.setName(garageModel.name());
        }

        if (garageModel.city() != null) {
            garage.setCity(garageModel.city());
        }

        if (garageModel.location() != null) {
            garage.setLocation(garageModel.location());
        }
    }

    private List<DailyAvailabilityReportModel> produceDailyAvailabilityReportInRange
            (Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate,
             LocalDate startDate, LocalDate endDate, Garage garage) {
        List<DailyAvailabilityReportModel> dailyAvailabilityReports = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (maintenancesForGivenDateExist(maintenancesGroupedByScheduledDate, currentDate)) {
                dailyAvailabilityReports.add(createReportWithRequests(garage, currentDate, maintenancesGroupedByScheduledDate));
            } else {
                dailyAvailabilityReports.add(createReportWithNoRequests(garage, currentDate));
            }
            currentDate = currentDate.plusDays(1);
        }
        return dailyAvailabilityReports;
    }

    private boolean maintenancesForGivenDateExist(Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate, LocalDate date) {
        return maintenancesGroupedByScheduledDate.containsKey(date);
    }

    private DailyAvailabilityReportModel createReportWithRequests(Garage garage, LocalDate date, Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate) {
        int maintenanceRequests = maintenancesGroupedByScheduledDate.get(date).size();
        int availableCapacity = garage.getCapacity();
        return new DailyAvailabilityReportModel(date, maintenanceRequests, availableCapacity);
    }

    private DailyAvailabilityReportModel createReportWithNoRequests(Garage garage, LocalDate date) {
        return new DailyAvailabilityReportModel(date, 0, garage.getCapacity());
    }
}
