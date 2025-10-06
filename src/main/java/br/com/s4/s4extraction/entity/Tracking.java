package br.com.s4.s4extraction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_tracking")
public class Tracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracking_id")
    private Long trackingId;

    // Map as simple foreign key fields to keep the scope minimal (no Spot/Tag entity coupling required)
    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "movement_time")
    private LocalDateTime movementTime;

    @Column(name = "external_id", nullable = false)
    private Long externalId;

    @PrePersist
    public void prePersist() {
        if (this.movementTime == null) {
            this.movementTime = LocalDateTime.now();
        }
    }
}
