package pu.fmi.car_management_system.exception;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(Integer carId) {
        super("Car with id:" + carId + " was not found");
    }
}
