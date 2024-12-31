package pu.fmi.car_management_system.infrastructure.services.car;

import pu.fmi.car_management_system.model.car.CarResponseModel;
import pu.fmi.car_management_system.model.car.CreateCarModel;
import pu.fmi.car_management_system.model.car.UpdateCarModel;

import java.util.List;

public interface CarService {

    List<CarResponseModel> readAll(String carMake, Integer garageId, Integer fromYear, Integer toYear);

    CarResponseModel readById(Integer id);

    CarResponseModel create(CreateCarModel createCarModel);

    CarResponseModel update(Integer id, UpdateCarModel updateCarModel);

    boolean delete(Integer id);
}
