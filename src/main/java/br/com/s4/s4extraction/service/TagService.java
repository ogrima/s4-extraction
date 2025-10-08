package br.com.s4.s4extraction.service;

import br.com.s4.s4extraction.entity.Tag;
import br.com.s4.s4extraction.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class TagService {

    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    public Tag create(Tag e) {
        validate(e);
        e.setTagId(null);
        return repository.save(e);
    }

    @Transactional(readOnly = true)
    public Tag getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tag not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Tag> getAll() {
        return repository.findAll();
    }

    public Tag update(Long id, Tag updated) {
        validate(updated);
        Tag current = getById(id);
        current.setTagName(updated.getTagName());
        current.setTagLabel(updated.getTagLabel());
        current.setHardwareId(updated.getHardwareId());
        current.setActive(updated.getActive());
        return repository.save(current);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Tag not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Tag> findByActive(Boolean active) {
        return repository.findByActive(active);
    }

    @Transactional(readOnly = true)
    public Tag findByHardwareId(String hardwareId) {
        Tag tag = repository.findByHardwareId(hardwareId);
        if (tag == null) {
            throw new IllegalArgumentException("Tag not found by hardwareId: " + hardwareId);
        }
        return tag;
    }

    private void validate(Tag e) {
        Assert.notNull(e, "Tag must not be null");
        Assert.hasText(e.getTagName(), "tagName is required");
    }
}
