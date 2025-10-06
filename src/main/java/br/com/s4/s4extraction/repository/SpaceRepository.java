package br.com.s4.s4extraction.repository;

import br.com.s4.s4extraction.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    Optional<Space> findBySpaceName(String spaceName);
    List<Space> findByActive(Boolean active);
}
