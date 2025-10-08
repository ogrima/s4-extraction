package br.com.s4.s4extraction.restfull;

import br.com.s4.s4extraction.entity.Tag;
import br.com.s4.s4extraction.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/search")
    public Tag findByHardwareId(@RequestParam("hardwareId") String hardwareId) {
        return tagService.findByHardwareId(hardwareId);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleIllegalArgs(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
