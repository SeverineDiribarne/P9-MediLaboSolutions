package com.medilabo.medilabo.controllers;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.medilabo.model.Gender;
import com.medilabo.medilabo.model.Patient;
import com.medilabo.medilabo.services.customUserDetailsService.CustomUserDetailsService;
import com.medilabo.medilabo.services.patientService.IPatientService;
import com.medilabo.medilabo.session.SessionStore;

@WebMvcTest(controllers = PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean IPatientService patientService;
    @MockBean CustomUserDetailsService customUserDetailsService;
    @MockBean SessionStore sessionStore;
    @Autowired ObjectMapper objectMapper;

    private Patient sample(long id) {
        return new Patient(id, "Doe", "John", "1980-01-01", Gender.M, "1 st", "0102030405");
    }

    @Test
    @DisplayName("GET /api/patient/list retourne la liste des patients")
    void list_ok() throws Exception {
        when(patientService.getPatientList()).thenReturn(List.of(sample(1), sample(2)));

        mockMvc.perform(get("/api/patient/list"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].firstname", is("John")));
    }

    @Test
    @DisplayName("GET /api/patient/details/{id} retourne 200 quand trouvé")
    void details_ok() throws Exception {
        when(patientService.getPatientById(5L)).thenReturn(Optional.of(sample(5)));

        mockMvc.perform(get("/api/patient/details/5"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.patientId", is(5)));
    }

    @Test
    @DisplayName("GET /api/patient/details/{id} retourne 404 avec payload d'erreur quand introuvable")
    void details_notFound() throws Exception {
        when(patientService.getPatientById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patient/details/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.status", is(404)))
               .andExpect(jsonPath("$.error", is("Not Found")))
               .andExpect(jsonPath("$.path", is("/api/patient/details/99")));
    }

    @Test
    @DisplayName("POST /api/patient/addpatient retourne le patient sauvegardé")
    void add_ok() throws Exception {
        Patient toSave = sample(0);
        Patient saved = sample(10);
        when(patientService.savePatient(any(Patient.class))).thenReturn(saved);

        mockMvc.perform(post("/api/patient/addpatient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toSave)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.patientId", is(10)));
    }

    @Test
    @DisplayName("POST /api/patient/update/{id} retourne 404 quand patient introuvable")
    void update_notFound() throws Exception {
        when(patientService.getPatientById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/patient/update/42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sample(0))))
               .andExpect(status().isNotFound())
               .andExpect(content().string(containsString("Patient not found")));
    }

    @Test
    @DisplayName("POST /api/patient/update/{id} retourne 200 et le patient mis à jour")
    void update_ok() throws Exception {
    when(patientService.getPatientById(5L)).thenReturn(Optional.of(sample(5)));
    Patient updated = new Patient(5, "Doe", "Alice", "1980-01-01", Gender.M, "1 st", "0102030405");
    when(patientService.updatePatient(eq(5L), any(Patient.class))).thenReturn(updated);

        mockMvc.perform(post("/api/patient/update/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstname").value("Alice"));
    }
}
