package pu.fmi.car_management_system.exception;

public class GarageNotFoundException extends RuntimeException {

    public GarageNotFoundException(Integer garageId) {
        super("Garage with id:" + garageId + " was not found");
    }
}
