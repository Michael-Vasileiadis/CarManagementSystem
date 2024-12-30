package pu.fmi.car_management_system.model.garage;

import java.time.LocalDate;

public record DailyAvailabilityReportModel(LocalDate date, Integer requests, Integer availableCapacity) {
}
