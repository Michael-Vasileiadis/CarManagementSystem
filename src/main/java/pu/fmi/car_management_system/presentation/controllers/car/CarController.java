package pu.fmi.car_management_system.presentation.controllers.car;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pu.fmi.car_management_system.infrastructure.services.car.CarService;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    //private final CarService carService;
}
