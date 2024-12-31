package pu.fmi.car_management_system.exception;

public class MaintenanceNotFoundException extends RuntimeException {

    public MaintenanceNotFoundException(Integer maintenanceId) {
        super("Maintenance with id:" + maintenanceId + " was not found");
    }
}
