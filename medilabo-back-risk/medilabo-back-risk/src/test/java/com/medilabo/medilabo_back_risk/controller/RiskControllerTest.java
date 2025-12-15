package com.medilabo.medilabo_back_risk.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.medilabo.medilabo_back_risk.dto.NotesDTO;
import com.medilabo.medilabo_back_risk.dto.PatientDTO;
import com.medilabo.medilabo_back_risk.model.Gender;
import com.medilabo.medilabo_back_risk.model.Note;
import com.medilabo.medilabo_back_risk.services.notesservice.NotesService;
import com.medilabo.medilabo_back_risk.services.patientservice.IPatientService;
import com.medilabo.medilabo_back_risk.services.riskservice.RiskService;

@WebMvcTest(controllers = RiskController.class)
@AutoConfigureMockMvc(addFilters = false)
class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private IPatientService patientService;

    @MockBean
    private NotesService notesService;

    @MockBean
    private RiskService riskService;

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} retourne le niveau de risque")
    void getDiabetesRisk_returnsRiskLevel() throws Exception {
        long patientId = 123L;

        // Mock appel REST patient: renvoie un body non vide
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET),
                                   any(HttpEntity.class),
                                   eq(String.class)))
            .thenReturn(ResponseEntity.ok("{\"id\":123,\"gender\":\"M\",\"birthdate\":\"1980-01-01\"}"));

        // Mock appel REST notes: renvoie un tableau de notes
        Note[] notesArray = new Note[] { new Note("n1", "123", "Doe", "Texte note") };
        when(restTemplate.exchange(contains("/api/notes/patient/" + patientId),
                                   eq(HttpMethod.GET),
                                   any(HttpEntity.class),
                                   eq(Note[].class)))
            .thenReturn(ResponseEntity.ok(notesArray));

        // Mock services de domaine
        PatientDTO dto = new PatientDTO();
        dto.setAge(40);
        dto.setGender(Gender.M);
        when(patientService.processPatientDTOData(any())).thenReturn(dto);
        when(notesService.processNotesDTOData(any(NotesDTO.class))).thenReturn(3);
        when(riskService.processRiskData(eq(dto), eq(3))).thenReturn("BORDERLINE");

        // Appel de l'endpoint
        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.diabetesRiskLevel").value("BORDERLINE"));
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> 404 si patient non trouvé")
    void getDiabetesRisk_patientNotFound() throws Exception {
        long patientId = 999L;

        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenThrow(org.springframework.web.client.HttpClientErrorException.create(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Not found",
                org.springframework.http.HttpHeaders.EMPTY,
                null,
                null
            ));

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> 500 si erreur inattendue sur patient")
    void getDiabetesRisk_errorOnPatient() throws Exception {
        long patientId = 1000L;
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RuntimeException("Boom"));

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> notes 404 => OK avec risque NONE (0 triggers)")
    void getDiabetesRisk_notesNotFound() throws Exception {
        long patientId = 200L;
        // Patient OK
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{\"id\":200,\"gender\":\"F\",\"birthdate\":\"1995-05-05\"}"));
        // Notes 404
        when(restTemplate.exchange(contains("/api/notes/patient/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(Note[].class)))
            .thenThrow(org.springframework.web.client.HttpClientErrorException.create(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Not found",
                org.springframework.http.HttpHeaders.EMPTY,
                null,
                null));
        PatientDTO dto = new PatientDTO();
        dto.setAge(30);
        dto.setGender(Gender.F);
        when(patientService.processPatientDTOData(any())).thenReturn(dto);
        // risk service doit être appelé avec triggerWordCount = 0
        when(riskService.processRiskData(eq(dto), eq(0))).thenReturn("NONE");

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.diabetesRiskLevel").value("NONE"));
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> erreur HTTP sur notes (500) => 500")
    void getDiabetesRisk_notesServerError() throws Exception {
        long patientId = 201L;
        // Patient OK
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{\"id\":201,\"gender\":\"M\",\"birthdate\":\"1990-01-01\"}"));
        // Notes 500
        when(restTemplate.exchange(contains("/api/notes/patient/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(Note[].class)))
            .thenThrow(org.springframework.web.client.HttpClientErrorException.create(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Server error",
                org.springframework.http.HttpHeaders.EMPTY,
                null,
                null));
        PatientDTO dto = new PatientDTO();
        dto.setAge(35);
        dto.setGender(Gender.M);
        when(patientService.processPatientDTOData(any())).thenReturn(dto);

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> exception runtime sur notes => 500")
    void getDiabetesRisk_notesRuntimeException() throws Exception {
        long patientId = 202L;
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{\"id\":202,\"gender\":\"M\",\"birthdate\":\"1985-12-12\"}"));
        when(restTemplate.exchange(contains("/api/notes/patient/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(Note[].class)))
            .thenThrow(new RuntimeException("Crash notes"));
        PatientDTO dto = new PatientDTO();
        dto.setAge(39);
        dto.setGender(Gender.M);
        when(patientService.processPatientDTOData(any())).thenReturn(dto);

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> patient body null => 404")
    void getDiabetesRisk_patientBodyNull() throws Exception {
        long patientId = 203L;
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok(null));

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> patient 403 => 403")
    void getDiabetesRisk_patientForbidden() throws Exception {
        long patientId = 204L;
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenThrow(org.springframework.web.client.HttpClientErrorException.create(
                org.springframework.http.HttpStatus.FORBIDDEN,
                "Forbidden",
                org.springframework.http.HttpHeaders.EMPTY,
                null,
                null));

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().isForbidden());
    }

    // ---- Tests supplémentaires pour augmenter la couverture des branches ----

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> JSON patient invalide => 500")
    void getDiabetesRisk_patientInvalidJson() throws Exception {
        long patientId = 205L;
        // Corps JSON invalide provoquant une JsonParseException lors du readValue
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{invalid"));

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> JSON patient vide {} => 404")
    void getDiabetesRisk_patientEmptyJson() throws Exception {
        long patientId = 206L;
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> patient non 2xx (500) sans exception => 404")
    void getDiabetesRisk_patientNon2xxResponseEntity() throws Exception {
        long patientId = 207L;
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.status(500).body("error"));

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> notes body null (2xx) => risque NONE")
    void getDiabetesRisk_notesNullBody() throws Exception {
        long patientId = 208L;
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{\"id\":208,\"gender\":\"M\",\"birthdate\":\"1988-08-08\"}"));
        // notes corps null -> liste vide
        when(restTemplate.exchange(contains("/api/notes/patient/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(Note[].class)))
            .thenReturn(ResponseEntity.ok(null));
        PatientDTO dto = new PatientDTO();
        dto.setAge(37);
        dto.setGender(Gender.M);
        when(patientService.processPatientDTOData(any())).thenReturn(dto);
        when(notesService.processNotesDTOData(any(NotesDTO.class))).thenReturn(0);
        when(riskService.processRiskData(eq(dto), eq(0))).thenReturn("NONE");

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.diabetesRiskLevel").value("NONE"));
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} -> notes 403 => 403")
    void getDiabetesRisk_notesForbidden() throws Exception {
        long patientId = 209L;
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{\"id\":209,\"gender\":\"F\",\"birthdate\":\"1992-02-02\"}"));
        when(restTemplate.exchange(contains("/api/notes/patient/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(Note[].class)))
            .thenThrow(org.springframework.web.client.HttpClientErrorException.create(
                org.springframework.http.HttpStatus.FORBIDDEN,
                "Forbidden notes",
                org.springframework.http.HttpHeaders.EMPTY,
                null,
                null));
        PatientDTO dto = new PatientDTO();
        dto.setAge(33);
        dto.setGender(Gender.F);
        when(patientService.processPatientDTOData(any())).thenReturn(dto);

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId))
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/risk/diabetes/{id} avec headers Authorization/User-Name/Session-Number")
    void getDiabetesRisk_withHeaders() throws Exception {
        long patientId = 210L;
        when(restTemplate.exchange(contains("/api/patient/details/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{\"id\":210,\"gender\":\"M\",\"birthdate\":\"1980-01-01\"}"));
        Note[] notesArray = new Note[] { new Note("n1", "210", "Doe", "Texte note") };
        when(restTemplate.exchange(contains("/api/notes/patient/" + patientId),
                                   eq(HttpMethod.GET), any(HttpEntity.class), eq(Note[].class)))
            .thenReturn(ResponseEntity.ok(notesArray));
        PatientDTO dto = new PatientDTO();
        dto.setAge(45);
        dto.setGender(Gender.M);
        when(patientService.processPatientDTOData(any())).thenReturn(dto);
        when(notesService.processNotesDTOData(any(NotesDTO.class))).thenReturn(2);
        when(riskService.processRiskData(eq(dto), eq(2))).thenReturn("BORDERLINE");

        mockMvc.perform(get("/api/risk/diabetes/{id}", patientId)
                .header("Authorization", "Bearer token")
                .header("User-Name", "tester")
                .header("Session-Number", "abc123"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.diabetesRiskLevel").value("BORDERLINE"));
    }
}
