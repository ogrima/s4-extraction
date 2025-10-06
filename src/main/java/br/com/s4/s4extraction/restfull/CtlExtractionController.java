package br.com.s4.s4extraction.restfull;

import br.com.s4.s4extraction.entity.CtlExtraction;
import br.com.s4.s4extraction.service.CtlExtractionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extractions")
public class CtlExtractionController {

    private final CtlExtractionService service;

    public CtlExtractionController(CtlExtractionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CtlExtraction> create(@RequestBody CtlExtraction body) {
        CtlExtraction created = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public CtlExtraction getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<CtlExtraction> getAll(@RequestParam(value = "spotId", required = false) Long spotId) {
        if (spotId != null) {
            return service.findBySpotId(spotId);
        }
        return service.getAll();
    }

    @PutMapping("/{id}")
    public CtlExtraction update(@PathVariable Long id, @RequestBody CtlExtraction body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleIllegalArgs(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
