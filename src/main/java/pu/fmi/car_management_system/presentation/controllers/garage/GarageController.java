package pu.fmi.car_management_system.presentation.controllers.garage;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pu.fmi.car_management_system.infrastructure.services.garage.GarageService;
import pu.fmi.car_management_system.model.garage.CreateGarageModel;
import pu.fmi.car_management_system.model.garage.DailyAvailabilityReportModel;
import pu.fmi.car_management_system.model.garage.GarageResponseModel;
import pu.fmi.car_management_system.model.garage.UpdateGarageModel;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/garages")
@RequiredArgsConstructor
public class GarageController {

    private final GarageService garageService;

    @GetMapping
    public List<GarageResponseModel> readAll(@RequestParam(required = false) String city) {
        return garageService.readAll(city);
    }

    @GetMapping("/{id}")
    public GarageResponseModel readById(@PathVariable Integer id) {
        return garageService.readById(id);
    }

    @GetMapping("/dailyAvailabilityReport")
    public List<DailyAvailabilityReportModel> getDailyAvailabilityReport(
            @RequestParam Integer garageId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return garageService.getDailyAvailabilityReport(garageId, startDate, endDate);
    }

    @PostMapping
    public GarageResponseModel create(@RequestBody CreateGarageModel createGarageModel) {
        return garageService.create(createGarageModel);
    }

    @PutMapping("/{id}")
    public GarageResponseModel update(@PathVariable Integer id, @RequestBody UpdateGarageModel updateGarageModel) {
        return garageService.update(id, updateGarageModel);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return garageService.delete(id);
    }
}
