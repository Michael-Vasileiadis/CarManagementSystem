package pu.fmi.car_management_system.domain.repository.car;

import jakarta.persistence.*;
import lombok.*;
import pu.fmi.car_management_system.domain.repository.garage.Garage;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "car")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String make;

    private String model;

    @Column(name = "production_year")
    private Integer productionYear;

    @Column(name = "license_plate")
    private String licensePlate;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "car_garages",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "garage_id")
    )
    private Set<Garage> garages = new HashSet<>();
}
