package br.com.s4.s4extraction.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String baseUrl;
    private final String login;
    private final String password;

    private volatile String session;

    public AuthService(RestTemplate restTemplate,
                       @Value("${s4.remote.base-url:https://unjubilantly-resorptive-fanny.ngrok-free.dev}") String baseUrl,
                       @Value("${s4.remote.login:admin}") String login,
                       @Value("${s4.remote.password:ebaotech123}") String password) {
        this.restTemplate = restTemplate;
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.login = login;
        this.password = password;
    }

    public synchronized String getValidSession() {
        if (StringUtils.hasText(this.session) && isSessionValid(this.session)) {
            return this.session;
        }
        this.session = doLogin();
        return this.session;
    }

    public String getLogin() {
        return login;
    }

    private boolean isSessionValid(String session) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("session", session);
            headers.add(HttpHeaders.COOKIE, buildCookieHeader(session));
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> resp = restTemplate.exchange(
                    baseUrl + "/session_is_valid.fcgi",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            return resp.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            return false;
        }
    }

    private String doLogin() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = new HashMap<>();
            body.put("login", login);
            body.put("password", password);
            String payload = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(
                    baseUrl + "/login.fcgi",
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

    public String buildCookieHeader(String session) {
        String host = baseUrl.replaceFirst("https?://", "");
        return "abuse_interstitial=" + host + "; login=" + urlEncode(login) + "; session=" + urlEncode(session);
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
