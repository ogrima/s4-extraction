package br.com.s4.s4extraction.repository;

import br.com.s4.s4extraction.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagName(String tagName);
    List<Tag> findByActive(Boolean active);

    Tag findByHardwareId(String cardValue);
}
