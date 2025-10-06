package br.com.s4.s4extraction.restfull;

import br.com.s4.s4extraction.entity.AccessLogs;
import br.com.s4.s4extraction.service.EventLoaderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventsController {

    private final EventLoaderService eventLoaderService;

    public EventsController(EventLoaderService eventLoaderService) {
        this.eventLoaderService = eventLoaderService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AccessLogs> loadAll(@RequestParam(name = "limit", defaultValue = "1000") int limit) throws JsonProcessingException {
        return eventLoaderService.loadAllEvents(limit);
    }

    @GetMapping(value = "/loadSince", produces = MediaType.APPLICATION_JSON_VALUE)
    public String loadSince(@RequestParam(name = "offset") int offset,
                            @RequestParam(name = "limit", defaultValue = "1000") int limit) throws JsonProcessingException {
        return eventLoaderService.loadEventsSince(offset, limit);
    }
}
