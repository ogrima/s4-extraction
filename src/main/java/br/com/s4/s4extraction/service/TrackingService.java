package br.com.s4.s4extraction.service;

import br.com.s4.s4extraction.entity.Tracking;
import br.com.s4.s4extraction.repository.TrackingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class TrackingService {

    private final TrackingRepository repository;

    public TrackingService(TrackingRepository repository) {
        this.repository = repository;
    }

    public Tracking create(Tracking e) {
        validate(e);
        e.setTrackingId(null);
        return repository.save(e);
    }

    @Transactional(readOnly = true)
    public Tracking getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tracking not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Tracking> getAll() {
        return repository.findAll();
    }

    public Tracking update(Long id, Tracking updated) {
        validate(updated);
        Tracking current = getById(id);
        current.setSpotId(updated.getSpotId());
        current.setTagId(updated.getTagId());
        current.setMovementTime(updated.getMovementTime());
        return repository.save(current);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Tracking not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Tracking> findBySpotId(Long spotId) {
        return repository.findBySpotId(spotId);
    }

    @Transactional(readOnly = true)
    public List<Tracking> findByTagId(Long tagId) {
        return repository.findByTagId(tagId);
    }

    private void validate(Tracking e) {
        Assert.notNull(e, "Tracking must not be null");
        Assert.notNull(e.getSpotId(), "spotId is required");
        Assert.notNull(e.getTagId(), "tagId is required");
    }
}
