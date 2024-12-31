package pu.fmi.car_management_system.presentation.controllers.maintentance;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pu.fmi.car_management_system.infrastructure.services.maintentance.MaintenanceService;
import pu.fmi.car_management_system.model.maintenance.MonthlyRequestsReportModel;
import pu.fmi.car_management_system.model.maintenance.CreateMaintenanceModel;
import pu.fmi.car_management_system.model.maintenance.MaintenanceResponseModel;
import pu.fmi.car_management_system.model.maintenance.UpdateMaintenanceModel;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping
    public List<MaintenanceResponseModel> readAll(
            @RequestParam(required = false) Integer carId,
            @RequestParam(required = false) Integer garageId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return maintenanceService.readAll(carId, garageId, startDate, endDate);
    }

    @GetMapping("/{id}")
    public MaintenanceResponseModel readById(@PathVariable Integer id) {
        return maintenanceService.readById(id);
    }

    @GetMapping("/monthlyRequestsReport")
    public List<MonthlyRequestsReportModel> getMonthlyRequestsReport(
            @RequestParam Integer garageId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth startMonth,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth endMonth) {
        return maintenanceService.getMonthlyRequestsReport(garageId, startMonth, endMonth);
    }

    @PostMapping
    public MaintenanceResponseModel create(@RequestBody CreateMaintenanceModel createMaintenanceDTO) {
        return maintenanceService.create(createMaintenanceDTO);
    }

    @PutMapping("/{id}")
    public MaintenanceResponseModel update(@PathVariable Integer id, @RequestBody UpdateMaintenanceModel updateMaintenanceDTO) {
        return maintenanceService.update(id, updateMaintenanceDTO);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return maintenanceService.delete(id);
    }
}
