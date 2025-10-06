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
@Table(name = "t_ctl_extraction")
public class CtlExtraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "extraction_id")
    private Long extractionId;

    @Column(name = "last_position", nullable = false)
    private Long lastPosition;

    @Column(name = "extraction_time")
    private LocalDateTime extractionTime;

    // Mapping as a simple field to avoid requiring t_spot entity in this project
    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @PrePersist
    public void prePersist() {
        if (this.extractionTime == null) {
            this.extractionTime = LocalDateTime.now();
        }
    }
}
