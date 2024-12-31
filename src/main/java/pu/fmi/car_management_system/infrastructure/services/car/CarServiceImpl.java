package pu.fmi.car_management_system.infrastructure.services.car;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pu.fmi.car_management_system.domain.repository.car.Car;
import pu.fmi.car_management_system.domain.repository.car.CarRepository;
import pu.fmi.car_management_system.domain.repository.garage.Garage;
import pu.fmi.car_management_system.domain.repository.garage.GarageRepository;
import pu.fmi.car_management_system.exception.CarNotFoundException;
import pu.fmi.car_management_system.exception.GarageNotFoundException;
import pu.fmi.car_management_system.model.car.CarResponseModel;
import pu.fmi.car_management_system.model.car.CreateCarModel;
import pu.fmi.car_management_system.model.car.UpdateCarModel;
import pu.fmi.car_management_system.model.garage.GarageResponseModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of all car service methods needed for the car controller
 */
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    private final GarageRepository garageRepository;

    public List<CarResponseModel> readAll(String carMake, Integer garageId, Integer fromYear, Integer toYear) {
        return carRepository.findByMakeAndGaragesId(carMake, garageId).stream()
                .filter(car -> isProductionYearInRange(car.getProductionYear(), fromYear, toYear))
                .map(this::mapToCarResponseModel)
                .toList();
    }

    public CarResponseModel readById(Integer id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));
        return mapToCarResponseModel(car);
    }

    @Transactional
    public CarResponseModel create(CreateCarModel createCarModel) {
        Set<Garage> garages = findGarages(createCarModel.garageIds());
        Car car = Car.builder()
                .make(createCarModel.make())
                .model(createCarModel.model())
                .productionYear(createCarModel.productionYear())
                .licensePlate(createCarModel.licensePlate())
                .garages(garages)
                .build();
        carRepository.save(car);
        return mapToCarResponseModel(car);
    }

    @Transactional
    public CarResponseModel update(Integer id, UpdateCarModel updateCarModel) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));
        updateFields(car, updateCarModel);
        return mapToCarResponseModel(car);
    }

    @Transactional
    public boolean delete(Integer id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));
        try {
            car.getGarages().forEach(garage -> garage.getCars().remove(car));
            carRepository.delete(car);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private CarResponseModel mapToCarResponseModel(Car car) {
        return new CarResponseModel(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getProductionYear(),
                car.getLicensePlate(),
                car.getGarages().stream()
                        .map(garage -> new GarageResponseModel(garage.getId(), garage.getName(),
                                garage.getLocation(), garage.getCity(), garage.getCapacity()))
                        .toList()
        );
    }

    private boolean isProductionYearInRange(Integer productionYear, Integer fromYear, Integer toYear) {
        boolean isAfterFromYear = true;
        if (fromYear != null) {
            isAfterFromYear = (productionYear >= fromYear);
        }
        boolean isBeforeToYear = true;
        if (toYear != null) {
            isBeforeToYear = (productionYear <= toYear);
        }
        return isAfterFromYear && isBeforeToYear;
    }

    private void updateFields(Car car, UpdateCarModel carModel) {
        if (carModel.make() != null) {
            car.setMake(carModel.make());
        }
        if (carModel.model() != null) {
            car.setMake(carModel.make());
        }
        if (carModel.productionYear() != null) {
            car.setProductionYear(carModel.productionYear());
        }
        if (carModel.licensePlate() != null) {
            car.setLicensePlate(carModel.licensePlate());
        }
        if (carModel.garageIds() != null) {
            car.setGarages(findGarages(carModel.garageIds()));
        }
    }

    private Set<Garage> findGarages(List<Integer> garageIds) {
        List<Garage> garages = garageRepository.findAllById(garageIds);

        Set<Integer> existingGarageIds = garages.stream()
                .map(Garage::getId)
                .collect(Collectors.toSet());
        List<Integer> missingGarageIds = garageIds.stream()
                .filter(garageId -> !existingGarageIds.contains(garageId))
                .toList();

        if (!missingGarageIds.isEmpty()) {
            throw new GarageNotFoundException(missingGarageIds);
        }

        return new HashSet<>(garages);
    }
}
