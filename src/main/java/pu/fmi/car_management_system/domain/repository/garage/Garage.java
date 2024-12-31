package pu.fmi.car_management_system.domain.repository.garage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pu.fmi.car_management_system.domain.repository.car.Car;

import java.util.Set;

@Entity
@Table(name = "garage")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String location;

    private String city;

    private Integer capacity;

    @ManyToMany(mappedBy = "garages", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Car> cars;
}
