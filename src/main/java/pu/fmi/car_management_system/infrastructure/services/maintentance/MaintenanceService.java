package pu.fmi.car_management_system.infrastructure.services.maintentance;

import pu.fmi.car_management_system.model.maintenance.CreateMaintenanceModel;
import pu.fmi.car_management_system.model.maintenance.MaintenanceResponseModel;
import pu.fmi.car_management_system.model.maintenance.MonthlyRequestsReportModel;
import pu.fmi.car_management_system.model.maintenance.UpdateMaintenanceModel;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface MaintenanceService {

    List<MaintenanceResponseModel> readAll(Integer carId, Integer garageId, LocalDate startDate, LocalDate endDate);

    MaintenanceResponseModel readById(Integer id);

    List<MonthlyRequestsReportModel> getMonthlyRequestsReport(Integer garageId, YearMonth startMonth, YearMonth endMonth);

    MaintenanceResponseModel create(CreateMaintenanceModel createMaintenanceModel);

    MaintenanceResponseModel update(Integer id, UpdateMaintenanceModel updateMaintenanceModel);

    boolean delete(Integer id);
}
