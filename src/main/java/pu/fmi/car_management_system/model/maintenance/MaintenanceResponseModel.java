package pu.fmi.car_management_system.model.maintenance;

import java.time.LocalDate;

public record MaintenanceResponseModel(Integer id, Integer carId, String carName, String serviceType,
                                       LocalDate scheduledDate, Integer garageId, String garageName) {
}
