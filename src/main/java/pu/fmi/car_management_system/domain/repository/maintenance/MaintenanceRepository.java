package pu.fmi.car_management_system.domain.repository.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Used for the persistence of maintenance data to the database
 */
@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {

    List<Maintenance> findByGarageId(Integer garageId);

    List<Maintenance> findByGarageIdAndScheduledDate(Integer garageId, LocalDate scheduledDate);

    List<Maintenance> findByGarageIdAndScheduledDateBetween(Integer garageId, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT m FROM Maintenance m
            WHERE (:garageId IS NULL OR m.garage.id = :garageId)
            AND (:carId IS NULL OR m.car.id = :carId)
            """)
    List<Maintenance> findByGarageIdAndCarId(Integer garageId, Integer carId);
}
