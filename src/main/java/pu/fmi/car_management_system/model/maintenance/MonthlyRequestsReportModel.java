package pu.fmi.car_management_system.model.maintenance;

import pu.fmi.car_management_system.model.maintenance.YearMonthModel;

public record MonthlyRequestsReportModel(YearMonthModel yearMonth, Integer requests) {
}
