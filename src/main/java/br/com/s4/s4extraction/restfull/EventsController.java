package br.com.s4.s4extraction.restfull;

import br.com.s4.s4extraction.service.EventLoaderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventsController {

    private final EventLoaderService eventLoaderService;

    public EventsController(EventLoaderService eventLoaderService) {
        this.eventLoaderService = eventLoaderService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String loadAll(@RequestParam(name = "limit", defaultValue = "1000") int limit) {
        return eventLoaderService.loadAllEvents(limit);
    }

    @GetMapping(value = "/loadSince", produces = MediaType.APPLICATION_JSON_VALUE)
    public String loadSince(@RequestParam(name = "offset") int offset,
                            @RequestParam(name = "limit", defaultValue = "1000") int limit) {
        return eventLoaderService.loadEventsSince(offset, limit);
    }
}
