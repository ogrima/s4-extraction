package br.com.s4.s4extraction.service;

import br.com.s4.s4extraction.entity.CtlExtraction;
import br.com.s4.s4extraction.repository.CtlExtractionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CtlExtractionService {

    private final CtlExtractionRepository repository;

    public CtlExtractionService(CtlExtractionRepository repository) {
        this.repository = repository;
    }

    public CtlExtraction create(CtlExtraction entity) {
        validate(entity);
        if (entity.getExtractionTime() == null) {
            entity.setExtractionTime(LocalDateTime.now());
        }
        entity.setExtractionId(null); // ensure new row
        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public CtlExtraction getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CtlExtraction not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<CtlExtraction> getAll() {
        return repository.findAll();
    }

    public CtlExtraction update(Long id, CtlExtraction updated) {
        validate(updated);
        CtlExtraction current = getById(id);
        current.setLastPosition(updated.getLastPosition());
        current.setSpotId(updated.getSpotId());
        // Update extraction time if provided; otherwise keep current
        if (updated.getExtractionTime() != null) {
            current.setExtractionTime(updated.getExtractionTime());
        }
        return repository.save(current);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("CtlExtraction not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CtlExtraction> findBySpotId(Long spotId) {
        return repository.findBySpotId(spotId);
    }

    private void validate(CtlExtraction e) {
        Assert.notNull(e, "Entity must not be null");
        Assert.notNull(e.getLastPosition(), "lastPosition is required");
        Assert.notNull(e.getSpotId(), "spotId is required");
    }
}
