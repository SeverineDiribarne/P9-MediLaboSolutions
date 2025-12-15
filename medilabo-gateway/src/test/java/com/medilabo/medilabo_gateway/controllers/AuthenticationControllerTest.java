package com.medilabo.medilabo_gateway.controllers;

import com.medilabo.medilabo_gateway.security.JwtBlackList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtBlackList jwtBlackList;

    @InjectMocks
    private AuthenticationController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new AuthenticationController(authenticationManager);
        // inject mock blacklist
        try {
            var field = AuthenticationController.class.getDeclaredField("jwtBlackList");
            field.setAccessible(true);
            field.set(controller, jwtBlackList);
        } catch (Exception ignored) {}
    }

    @Test
    void authenticate_success_returnsAccessToken() {
        Jwt jwt = new Jwt("abc.def.ghi", Instant.now(), Instant.now().plusSeconds(3600), Map.of("alg","HS256"), Map.of("sub","user"));
        Authentication auth = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);

        ResponseEntity<?> response = controller.authenticate(new AuthenticationController.LoginRequest("user", "pwd"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String,String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("accessToken")).isEqualTo("abc.def.ghi");
    }

    @Test
    void authenticate_unauthorized_whenNotAuthenticated() {
        Authentication unauth = new UsernamePasswordAuthenticationToken("user","pwd");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(unauth);

        ResponseEntity<?> response = controller.authenticate(new AuthenticationController.LoginRequest("user", "pwd"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void logout_blacklistsToken_onValidHeader() {
        ResponseEntity<?> response = controller.logout("Bearer token-123");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(jwtBlackList).blackList("token-123");
    }

    @Test
    void logout_badRequest_onInvalidHeader() {
        ResponseEntity<?> response = controller.logout("InvalidHeader");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
