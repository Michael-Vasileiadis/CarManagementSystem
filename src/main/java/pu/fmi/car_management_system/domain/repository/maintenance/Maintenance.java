package pu.fmi.car_management_system.domain.repository.maintenance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pu.fmi.car_management_system.domain.repository.car.Car;
import pu.fmi.car_management_system.domain.repository.garage.Garage;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne
    @JoinColumn(name = "garage_id", nullable = false)
    private Garage garage;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;
}
