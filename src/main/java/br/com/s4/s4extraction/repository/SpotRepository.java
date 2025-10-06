package br.com.s4.s4extraction.repository;

import br.com.s4.s4extraction.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    List<Spot> findBySpaceId(Long spaceId);
    Optional<Spot> findBySpotName(String spotName);
    List<Spot> findByActive(Boolean active);
    Optional<Spot> findByHardwareId(String hardwareId);
}
