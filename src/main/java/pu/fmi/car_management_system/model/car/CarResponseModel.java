package pu.fmi.car_management_system.model.car;

import pu.fmi.car_management_system.model.garage.GarageResponseModel;

import java.util.List;

public record CarResponseModel(Integer id, String make, String model, Integer productionYear,
                               String licensePlate, List<GarageResponseModel> garages) {
}
