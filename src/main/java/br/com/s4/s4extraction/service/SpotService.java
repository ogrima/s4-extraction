package br.com.s4.s4extraction.service;

import br.com.s4.s4extraction.entity.Spot;
import br.com.s4.s4extraction.repository.SpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class SpotService {

    private final SpotRepository repository;

    public SpotService(SpotRepository repository) {
        this.repository = repository;
    }

    public Spot create(Spot e) {
        validate(e);
        e.setSpotId(null);
        return repository.save(e);
    }

    @Transactional(readOnly = true)
    public Spot getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Spot not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Spot> getAll() {
        return repository.findAll();
    }

    public Spot update(Long id, Spot updated) {
        validate(updated);
        Spot current = getById(id);
        current.setSpotName(updated.getSpotName());
        current.setSpotLabel(updated.getSpotLabel());
        current.setHardwareId(updated.getHardwareId());
        current.setActive(updated.getActive());
        current.setSpaceId(updated.getSpaceId());
        current.setCritical(updated.getCritical());
        return repository.save(current);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Spot not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Spot> findBySpaceId(Long spaceId) {
        return repository.findBySpaceId(spaceId);
    }

    @Transactional(readOnly = true)
    public List<Spot> findByActive(Boolean active) {
        return repository.findByActive(active);
    }

    private void validate(Spot e) {
        Assert.notNull(e, "Spot must not be null");
        Assert.hasText(e.getSpotName(), "spotName is required");
        Assert.notNull(e.getSpaceId(), "spaceId is required");
        Assert.notNull(e.getCritical(), "critical is required");
    }
}
