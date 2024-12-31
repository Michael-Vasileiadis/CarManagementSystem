package pu.fmi.car_management_system.presentation.controllers.car;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pu.fmi.car_management_system.infrastructure.services.car.CarService;
import pu.fmi.car_management_system.model.car.CarResponseModel;
import pu.fmi.car_management_system.model.car.CreateCarModel;
import pu.fmi.car_management_system.model.car.UpdateCarModel;

import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public List<CarResponseModel> readAll(
            @RequestParam(required = false) String carMake,
            @RequestParam(required = false) Integer garageId,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear
    ) {
        return carService.readAll(carMake, garageId, fromYear, toYear);
    }

    @GetMapping("/{id}")
    public CarResponseModel readById(@PathVariable Integer id) {
        return carService.readById(id);
    }

    @PostMapping
    public CarResponseModel create(@RequestBody CreateCarModel createCarModel) {
        return carService.create(createCarModel);
    }

    @PutMapping("/{id}")
    public CarResponseModel update(@PathVariable Integer id, @RequestBody UpdateCarModel updateCarModel) {
        return carService.update(id, updateCarModel);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return carService.delete(id);
    }
}
