package pu.fmi.car_management_system.exception;

import java.util.List;

public class GarageNotFoundException extends RuntimeException {

    public GarageNotFoundException(Integer garageId) {
        super("Garage with id:" + garageId + " was not found");
    }

    public GarageNotFoundException(List<Integer> ids) {
        super("Garages with the following ids are missing: " + ids);
    }
}
