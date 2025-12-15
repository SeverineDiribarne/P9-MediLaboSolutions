package com.medilabo.medilabo.controllers;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.medilabo.model.CustomUserDetails;
import com.medilabo.medilabo.model.User;
import com.medilabo.medilabo.model.UserAuthorities;
import com.medilabo.medilabo.services.jwt.JwtService;
import com.medilabo.medilabo.session.SessionStore;
import com.medilabo.medilabo.services.customUserDetailsService.CustomUserDetailsService;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthenticationManager authenticationManager;
    @MockBean SessionStore sessionStore;
    @MockBean JwtService jwtService;
    @MockBean CustomUserDetailsService customUserDetailsService;

    record LoginReq(String username, String password) {}

    private CustomUserDetails sampleUserDetails() {
        User user = new User(1L, "john@doe", "pwd", true, false, true, true, UserAuthorities.USER);
        return new CustomUserDetails(user);
    }

    @Test
    @DisplayName("POST /authentication succès retourne accessToken + sessionNumber")
    void auth_ok() throws Exception {
        CustomUserDetails cud = sampleUserDetails();
        Authentication authResponse = UsernamePasswordAuthenticationToken.authenticated(
            cud, "pwd", cud.getAuthorities());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authResponse);
        when(jwtService.generateSignedJwtToken(any())).thenReturn("token");
        // SessionStore.put est void, juste vérifier l'appel

        mockMvc.perform(post("/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginReq("john@doe", "pwd"))))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.accessToken", is("token")))
               .andExpect(jsonPath("$.sessionNumber", not(emptyOrNullString())));

        verify(sessionStore).put(anyString(), eq("john@doe"));
    }

    @Test
    @DisplayName("POST /authentication échec retourne 401")
    void auth_unauthorized() throws Exception {
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenThrow(new BadCredentialsException("bad"));

        mockMvc.perform(post("/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginReq("john@doe", "wrong"))))
               .andExpect(status().isUnauthorized())
               .andExpect(content().string(containsString("Invalid username or password")));
    }
}
