package pu.fmi.car_management_system.domain.repository.car;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Used for the persistence of car data to the database
 */
public interface CarRepository extends JpaRepository<Car, Integer> {

    @Query("""
            SELECT c FROM Car c
            LEFT JOIN c.garages g
            WHERE (:make IS NULL OR c.make = :make)
            AND (:garageId IS NULL OR g.id = :garageId)
            """)
    List<Car> findByMakeAndGaragesId(String make, Integer garageId);
}
