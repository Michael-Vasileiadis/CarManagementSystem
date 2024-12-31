package pu.fmi.car_management_system.domain.repository.garage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Used for the persistence of garage data to the database
 */
@Repository
public interface GarageRepository extends JpaRepository<Garage, Integer> {

    List<Garage> findByCity(String city);
}
