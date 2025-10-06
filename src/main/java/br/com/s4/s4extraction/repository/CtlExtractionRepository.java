package br.com.s4.s4extraction.repository;

import br.com.s4.s4extraction.entity.CtlExtraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CtlExtractionRepository extends JpaRepository<CtlExtraction, Long> {
    List<CtlExtraction> findBySpotId(Long spotId);
}
