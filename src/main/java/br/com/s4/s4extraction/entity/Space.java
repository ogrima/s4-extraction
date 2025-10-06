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
@Table(name = "t_space")
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_id")
    private Long spaceId;

    @Column(name = "space_name", nullable = false, unique = true)
    private String spaceName;

    @Column(name = "space_label")
    private String spaceLabel;

    @Column(name = "active")
    private Boolean active;
}
