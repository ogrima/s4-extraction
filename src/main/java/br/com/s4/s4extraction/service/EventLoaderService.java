package br.com.s4.s4extraction.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventLoaderService {

    private final RestTemplate restTemplate;
    private final AuthService authService;
    private final String baseUrl;

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
    public String loadAllEvents(int limit) {
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

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("loadAllEvents failed with status " + resp.getStatusCode());
        }
        return resp.getBody();
    }

    /**
     * Calls load_objects.fcgi including an offset parameter to load events since a given position.
     */
    public String loadEventsSince(int offset, int limit) {
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

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("loadEventsSince failed with status " + resp.getStatusCode());
        }
        return resp.getBody();
    }

    private static String trimTrailingSlash(String v) {
        if (v == null) return null;
        if (v.endsWith("/")) return v.substring(0, v.length() - 1);
        return v;
    }
}
