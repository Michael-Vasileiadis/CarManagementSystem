package pu.fmi.car_management_system.model.maintenance;

import java.time.LocalDate;

public record UpdateMaintenanceModel(Integer carId, String serviceType, LocalDate scheduledDate, Integer garageId) {
}
