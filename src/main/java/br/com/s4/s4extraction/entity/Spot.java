package br.com.s4.s4extraction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_spot")
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spot_id")
    private Long spotId;

    @Column(name = "spot_name", nullable = false, unique = true)
    private String spotName;

    @Column(name = "spot_label")
    private String spotLabel;

    @Column(name = "hardware_id")
    private String hardwareId;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(name = "critical", nullable = false)
    private Boolean critical;
}
