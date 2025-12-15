package com.medilabo_gui.medilabo_gui.providers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.medilabo_gui.medilabo_gui.model.User;
import com.medilabo_gui.medilabo_gui.utils.JwtUtils;

import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private RestTemplate restTemplate;

    private String gatewayUrl;

//Foldback value in case the property is not loaded
    public JwtAuthenticationProvider(@Value("${gateway.url:https://medilabo-gateway:8090}") String gatewayUrl) {
        this.gatewayUrl = gatewayUrl; // URL du service de validation du token
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        User userToCheck = new User();
        userToCheck.setUsername(authentication.getName());
        userToCheck.setPassword(authentication.getCredentials().toString());

        try {
            HttpEntity<User> entity = new HttpEntity<>(userToCheck, headers);
            ResponseEntity<String> entityResponseEntity = restTemplate
                    .postForEntity(gatewayUrl + "/authentication", entity, String.class);

            String responseBody = entityResponseEntity.getBody();
            if (entityResponseEntity.getStatusCode().is2xxSuccessful()
                    && responseBody != null && !responseBody.isEmpty()) {
                Map<String, Object> json = new BasicJsonParser().parseMap(responseBody);
                String jwtTokenReceived = (String) json.get("accessToken");

                List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtTokenReceived);
                UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(
                        authentication.getName(), authentication.getCredentials().toString(),
                        authorities);
                authResult.setDetails(jwtTokenReceived);
                return authResult;
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