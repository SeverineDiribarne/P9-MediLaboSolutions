package com.medilabo.medilabo_gateway.config;

import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.json.BasicJsonParser;

import com.medilabo.medilabo_gateway.models.User;
import com.medilabo.medilabo_gateway.session.SessionStore;
import com.medilabo.medilabo_gateway.utils.JwtUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MyCustomAuthenticationProvider implements AuthenticationProvider {

    private final String serverUrl;

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private RestTemplate restTemplate;

    MyCustomAuthenticationProvider(@Value("${server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        User userToCheck = new User(authentication.getName(), authentication.getCredentials().toString());
        try {
            HttpEntity<User> entity = new HttpEntity<>(userToCheck, headers);
            ResponseEntity<String> entityResponseEntity = restTemplate
                    .postForEntity(serverUrl + "/authentication",
                            entity,
                            String.class);
            if (entityResponseEntity.getStatusCode().is2xxSuccessful()) {
                String body = entityResponseEntity.getBody();
                if (body != null && !body.isEmpty()) {
                    Map<String, Object> json = new BasicJsonParser()
                            .parseMap(entityResponseEntity.getBody());
                    String jwtTokenReceived = (String) json.get("accessToken");
                    String sessionNumber = (String) json.get("sessionNumber");
                    String username = authentication.getName();
                    if (sessionNumber != null && username != null) {
                        sessionStore.put(sessionNumber, username);
                    }
                    // Parse JWT header and claims (no signature verification)
                    String[] parts = jwtTokenReceived.split("\\.");
                    if (parts.length < 2) {
                        throw new BadCredentialsException("Invalid JWT format");
                    }
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> headersMap = mapper.readValue(
                                new String(Base64.getUrlDecoder().decode(parts[0])),
                                new TypeReference<Map<String, Object>>() {
                                });
                        Map<String, Object> claimsMap = mapper.readValue(
                                new String(Base64.getUrlDecoder().decode(parts[1])),
                                new TypeReference<Map<String, Object>>() {
                                });
                        List<String> authoritiesList = JwtUtils
                                .decodeAuthorities(jwtTokenReceived);
                        List<GrantedAuthority> authorities = authoritiesList.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                        Jwt.Builder jwtBuilder = Jwt.withTokenValue(jwtTokenReceived)
                                .headers(h -> h.putAll(headersMap));
                        for (Map.Entry<String, Object> entry : claimsMap.entrySet()) {
                            String key = entry.getKey();
                            Object value = entry.getValue();

                            // Convert numeric timestamps to Instant
                            if ((key.equals("exp") || key.equals("iat") || key.equals("nbf"))
                                    && value instanceof Number) {
                                value = Instant.ofEpochSecond(((Number) value).longValue());
                            }
                            jwtBuilder.claim(key, value);
                        }
                        Jwt jwt = jwtBuilder.build();
                        return new JwtAuthenticationToken(jwt, authorities);
                }
            }
        } catch (RestClientException e) {
            throw new BadCredentialsException("Authentication failed", e);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Authentication error", e);
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}