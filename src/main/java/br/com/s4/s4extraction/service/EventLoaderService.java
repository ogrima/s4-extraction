package br.com.s4.s4extraction.service;

import br.com.s4.s4extraction.entity.AccessLogs;
import br.com.s4.s4extraction.entity.CtlExtraction;
import br.com.s4.s4extraction.entity.Tag;
import br.com.s4.s4extraction.entity.Tracking;
import br.com.s4.s4extraction.repository.CtlExtractionRepository;
import br.com.s4.s4extraction.repository.TagRepository;
import br.com.s4.s4extraction.repository.TrackingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class EventLoaderService {

    private final RestTemplate restTemplate;
    private final AuthService authService;
    private final String baseUrl;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TrackingRepository trackingRepository;
    @Autowired
    private CtlExtractionRepository ctlExtractionRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public EventLoaderService(RestTemplate restTemplate, AuthService authService,
                              @org.springframework.beans.factory.annotation.Value("${s4.remote.base-url:https://unjubilantly-resorptive-fanny.ngrok-free.dev}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.authService = authService;
        this.baseUrl = trimTrailingSlash(baseUrl);
    }

    /**
     * Calls load_objects.fcgi with proper authentication headers using AuthService.
     * Returns raw JSON string from remote API.
     */
    public List<AccessLogs> loadAllEvents(int limit) throws JsonProcessingException {
        String url = baseUrl + "/load_objects.fcgi";
        String session = authService.getValidSession();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("session", session);
        headers.add(HttpHeaders.COOKIE, authService.buildCookieHeader(session));

        Map<String, Object> body = new HashMap<>();
        body.put("object", "access_logs");
        body.put("ordendinger_by", "-time"); // keeping the provided key as-is
        body.put("limit", limit);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body) , headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("loadAllEvents failed with status " + resp.getStatusCode());
        }
        return parseEvents(objectMapper.writeValueAsString(resp.getBody().get("access_logs")));
    }

    /**
     * Calls load_objects.fcgi including an offset parameter to load events since a given position.
     */
    public  List<AccessLogs> loadEventsSince(int offset, int limit) throws JsonProcessingException {
        String url = baseUrl + "/load_objects.fcgi";
        String session = authService.getValidSession();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("session", session);
        headers.add(HttpHeaders.COOKIE, authService.buildCookieHeader(session));

        Map<String, Object> body = new HashMap<>();
        body.put("object", "access_logs");
        body.put("offset", offset);
        body.put("ordendinger_by", "-time");
        body.put("limit", limit);

        HttpEntity<String> entity = new HttpEntity<>( objectMapper.writeValueAsString(body), headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("loadEventsSince failed with status " + resp.getStatusCode());
        }
        return parseEvents(objectMapper.writeValueAsString(resp.getBody().get("access_logs")));
    }

    private static String trimTrailingSlash(String v) {
        if (v == null) return null;
        if (v.endsWith("/")) return v.substring(0, v.length() - 1);
        return v;
    }

    public List<AccessLogs> parseEvents(String rawJson) throws JsonProcessingException {
        return objectMapper.readValue(rawJson, objectMapper.getTypeFactory().constructCollectionType(List.class, AccessLogs.class));
    }

    public void loadTrackingEvents(List<AccessLogs> result, Long spotId, CtlExtraction row) {

        List<AccessLogs> filteredResult = result.stream()
                .filter(item -> item.getUserId() != null && item.getUserId() != 0)
                .toList();

        if (filteredResult.isEmpty()) {
            Long maxId = result.stream()
                    .mapToLong(AccessLogs::getId)
                    .max()
                    .orElse(0L);

            row.setLastPosition(maxId);
            row.setExtractionTime(LocalDateTime.now());
            ctlExtractionRepository.save(row);
            return;
        }

        AtomicBoolean updateQueue = new AtomicBoolean(false);

        filteredResult.forEach(item -> {
            Tag tag = tagRepository.findByHardwareId(String.valueOf(item.getUserId()));
            if (tag != null) {
                Tracking tracking = Tracking.builder()
                        .spotId(spotId)
                        .tagId(tag.getTagId())
                        .movementTime(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(item.getTime()),
                                ZoneId.systemDefault()))
                        .externalId(item.getId())
                        .build();
                trackingRepository.save(tracking);
                updateQueue.set(true);
            }
        });

        if (updateQueue.get()) {

            Long maxId = filteredResult.stream()
                    .mapToLong(AccessLogs::getId)
                    .max()
                    .orElse(0L);

            row.setLastPosition(maxId);
            row.setExtractionTime(LocalDateTime.now());
            ctlExtractionRepository.save(row);
        }
    }

    public void trackingFirstLoad(List<AccessLogs> result, Long spotId) {

        List<AccessLogs> filteredResult = result.stream()
                .filter(item -> item.getUserId() != null && item.getUserId() != 0)
                .toList();

        if (filteredResult.isEmpty()) {
            Long maxId = result.stream()
                    .mapToLong(AccessLogs::getId)
                    .max()
                    .orElse(0L);

            CtlExtraction ctlExtraction = CtlExtraction.builder()
                    .spotId(spotId)
                    .lastPosition(maxId)
                    .build();

            ctlExtractionRepository.save(ctlExtraction);
            return;
        }

        AtomicBoolean updateQueue = new AtomicBoolean(false);

        filteredResult.forEach(item -> {
            Tag tag = tagRepository.findByHardwareId(String.valueOf(item.getUserId()));
            if (tag != null) {
                Tracking tracking = Tracking.builder()
                        .spotId(spotId)
                        .tagId(tag.getTagId())
                        .movementTime(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(item.getTime()),
                                ZoneId.systemDefault()))
                        .externalId(item.getId())
                        .build();
                trackingRepository.save(tracking);
                updateQueue.set(true);
            }
        });

        if (updateQueue.get()) {

            Long maxId = filteredResult.stream()
                    .mapToLong(AccessLogs::getId)
                    .max()
                    .orElse(0L);

            CtlExtraction ctlExtraction = CtlExtraction.builder()
                    .spotId(spotId)
                    .lastPosition(maxId)
                    .build();

            ctlExtractionRepository.save(ctlExtraction);
      }

    }

}
