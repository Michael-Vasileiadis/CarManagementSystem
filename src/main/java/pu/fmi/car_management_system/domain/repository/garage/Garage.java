package pu.fmi.car_management_system.domain.repository.garage;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "garage")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Garage {
}
