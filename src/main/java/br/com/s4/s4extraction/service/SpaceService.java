package br.com.s4.s4extraction.service;

import br.com.s4.s4extraction.entity.Space;
import br.com.s4.s4extraction.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class SpaceService {

    private final SpaceRepository repository;

    public SpaceService(SpaceRepository repository) {
        this.repository = repository;
    }

    public Space create(Space e) {
        validate(e);
        e.setSpaceId(null);
        return repository.save(e);
    }

    @Transactional(readOnly = true)
    public Space getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Space not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Space> getAll() {
        return repository.findAll();
    }

    public Space update(Long id, Space updated) {
        validate(updated);
        Space current = getById(id);
        current.setSpaceName(updated.getSpaceName());
        current.setSpaceLabel(updated.getSpaceLabel());
        current.setActive(updated.getActive());
        return repository.save(current);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Space not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Space> findByActive(Boolean active) {
        return repository.findByActive(active);
    }

    private void validate(Space e) {
        Assert.notNull(e, "Space must not be null");
        Assert.hasText(e.getSpaceName(), "spaceName is required");
    }
}
