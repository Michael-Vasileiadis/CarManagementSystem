package pu.fmi.car_management_system.model.car;

import java.util.List;

public record UpdateCarModel(String make, String model, Integer productionYear, String licensePlate, List<Integer> garageIds) {
}
