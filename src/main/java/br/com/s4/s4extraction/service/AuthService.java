package br.com.s4.s4extraction.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import br.com.s4.s4extraction.repository.SpotRepository;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SpotRepository spotRepository;

    private final String login;
    private final String password;

    // keep independent sessions per spotId
    private final Map<Long, String> sessions = new ConcurrentHashMap<>();

    public AuthService(RestTemplate restTemplate,
                       @Value("${s4.remote.login:admin}") String login,
                       @Value("${s4.remote.password:ebaotech123}") String password) {
        this.restTemplate = restTemplate;
        this.login = login;
        this.password = password;
    }

    public synchronized String getValidSession(Long spotId) {
        String current = sessions.get(spotId);
        if (StringUtils.hasText(current) && isSessionValid(spotId, current)) {
            return current;
        }
        String newSession = doLogin(spotId);
        sessions.put(spotId, newSession);
        return newSession;
    }

    public String getLogin() {
        return login;
    }

    private boolean isSessionValid(Long spotId, String session) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("session", session);
            headers.add(HttpHeaders.COOKIE, buildCookieHeader(spotId, session));
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> resp = restTemplate.exchange(
                    getBaseUrl(spotId) + "/session_is_valid.fcgi",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            return resp.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            return false;
        }
    }

    private String doLogin(Long spotId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = new HashMap<>();
            body.put("login", login);
            body.put("password", password);
            String payload = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(
                    getBaseUrl(spotId) + "/login.fcgi",
                    entity,
                    Map.class
            );
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new IllegalStateException("Login failed with status " + resp.getStatusCode());
            }
            JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(resp.getBody()));
            JsonNode sessionNode = node.get("session");
            if (sessionNode == null || !sessionNode.isTextual()) {
                throw new IllegalStateException("Login response missing 'session' field");
            }
            return sessionNode.asText();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to authenticate: " + e.getMessage(), e);
        }
    }

    public String buildCookieHeader(Long spotId, String session) {
        String host = getBaseUrl(spotId).replaceFirst("https?://", "");
        return "abuse_interstitial=" + host + "; login=" + urlEncode(login) + "; session=" + urlEncode(session);
    }

    private String getBaseUrl(Long spotId) {
        return spotRepository.findById(spotId)
                .map(spot -> {
                    String label = spot.getSpotLabel();
                    if (label == null || label.isBlank()) {
                        throw new IllegalStateException("Spot label is empty for spotId: " + spotId);
                    }
                    if (label.startsWith("http")) {
                        return trimTrailingSlash(label);
                    }
                    return "http://" + trimTrailingSlash(label);
                })
                .orElseThrow(() -> new IllegalArgumentException("Spot not found for spotId: " + spotId));
    }

    private static String urlEncode(String v) {
        return java.net.URLEncoder.encode(v, StandardCharsets.UTF_8);
    }

    private static String trimTrailingSlash(String v) {
        if (v == null) return null;
        if (v.endsWith("/")) return v.substring(0, v.length() - 1);
        return v;
    }
}
