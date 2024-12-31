package pu.fmi.car_management_system.infrastructure.services.garage;

import jakarta.annotation.Nullable;
import pu.fmi.car_management_system.model.garage.CreateGarageModel;
import pu.fmi.car_management_system.model.garage.DailyAvailabilityReportModel;
import pu.fmi.car_management_system.model.garage.GarageResponseModel;
import pu.fmi.car_management_system.model.garage.UpdateGarageModel;

import java.time.LocalDate;
import java.util.List;

public interface GarageService {

    List<GarageResponseModel> readAll(@Nullable String city);

    GarageResponseModel readById(Integer id);

    List<DailyAvailabilityReportModel> getDailyAvailabilityReport(Integer garageId, LocalDate startDate, LocalDate endDate);

    GarageResponseModel create(CreateGarageModel createGarageModel);

    GarageResponseModel update(Integer id, UpdateGarageModel createGarageModel);

    boolean delete(Integer id);
}
