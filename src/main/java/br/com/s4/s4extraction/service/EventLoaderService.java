package br.com.s4.s4extraction.service;

import br.com.s4.s4extraction.entity.AccessLogs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventLoaderService {

    private final RestTemplate restTemplate;
    private final AuthService authService;
    private final String baseUrl;

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
        return parseEvents(objectMapper.writeValueAsString(resp.getBody()));
    }

    /**
     * Calls load_objects.fcgi including an offset parameter to load events since a given position.
     */
    public String loadEventsSince(int offset, int limit) throws JsonProcessingException {
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
        return objectMapper.writeValueAsString(resp.getBody());
    }

    private static String trimTrailingSlash(String v) {
        if (v == null) return null;
        if (v.endsWith("/")) return v.substring(0, v.length() - 1);
        return v;
    }

    public List<AccessLogs> parseEvents(String rawJson) throws JsonProcessingException {
        return objectMapper.readValue(rawJson, objectMapper.getTypeFactory().constructCollectionType(List.class, AccessLogs.class));
    }

}
