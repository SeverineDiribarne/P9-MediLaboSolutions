package com.medilabo.medilabo.controllers;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.medilabo.medilabo.dto.UserPublicDTO;
import com.medilabo.medilabo.services.customUserDetailsService.CustomUserDetailsService;
import com.medilabo.medilabo.services.userService.IUserService;
import com.medilabo.medilabo.session.SessionStore;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean IUserService userService;
    @MockBean CustomUserDetailsService customUserDetailsService;
    @MockBean SessionStore sessionStore;

    @Test
    @DisplayName("GET /api/users retourne la liste des utilisateurs publics")
    void users_ok() throws Exception {
        when(userService.findAll()).thenReturn(List.of(
            new UserPublicDTO("a@a", List.of("ROLE_ADMIN")),
            new UserPublicDTO("b@b", List.of("ROLE_USER", "ROLE_ADMIN"))
        ));

     mockMvc.perform(get("/api/users"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$", hasSize(2)))
         .andExpect(jsonPath("$[0].username").value("a@a"))
         .andExpect(jsonPath("$[0].authorities[0]").value("ROLE_ADMIN"))
         .andExpect(jsonPath("$[1].authorities", hasItems("ROLE_USER", "ROLE_ADMIN")));
    }
}
