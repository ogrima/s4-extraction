package br.com.s4.s4extraction.repository;

import br.com.s4.s4extraction.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {
    List<Tracking> findBySpotId(Long spotId);
    List<Tracking> findByTagId(Long tagId);
    List<Tracking> findBySpotIdAndTagId(Long spotId, Long tagId);
    List<Tracking> findByMovementTimeAfter(LocalDateTime after);
}
