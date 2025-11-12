package com.medilabo.medilabo_gateway.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.medilabo_gateway.dto.NoteDTO;
import com.medilabo.medilabo_gateway.dto.PatientDTO;
import com.medilabo.medilabo_gateway.models.Gender;
import com.medilabo.medilabo_gateway.session.SessionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked"})
class PatientControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SessionStore sessionStore;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PatientController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(sessionStore.getSessionNumber(anyString())).thenReturn("session-123");
    }

    @Test
    void getUsers_success_parsesJson() throws Exception {
        String json = "[{\"patientId\":1,\"lastname\":\"Doe\",\"firstname\":\"John\"}]";
        ResponseEntity<String> resp = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(resp);

        List<PatientDTO> expected = List.of(new PatientDTO(1L, "Doe", "John", "", Gender.M, " ", " ", null));
        when(objectMapper.readValue(eq(json), any(TypeReference.class))).thenReturn(expected);

        ResponseEntity<?> response = controller.getUsers(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>) response.getBody()).hasSize(1);
    }

    @Test
    void getUsers_forbidden_returns403() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden"));

        ResponseEntity<?> response = controller.getUsers(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getUsers_otherClientError_propagatesStatus() {
        HttpStatusCode status = HttpStatus.NOT_FOUND;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(status, "Not found", (byte[]) null, StandardCharsets.UTF_8));

        ResponseEntity<?> response = controller.getUsers(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getUsers_objectMapperThrows_returns500() throws Exception {
        String json = "[]";
        ResponseEntity<String> resp = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(resp);
        when(objectMapper.readValue(eq(json), any(TypeReference.class))).thenThrow(new RuntimeException("boom"));

        ResponseEntity<?> response = controller.getUsers(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getPatientById_success_withNotes() {
        PatientDTO dto = new PatientDTO(1L, "Doe", "Jane", "1990-09-05", Gender.F, " ", " ", null);
        ResponseEntity<PatientDTO> patientResp = new ResponseEntity<>(dto, HttpStatus.OK);
        when(restTemplate.exchange(contains("/api/patient/details/"), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(patientResp);

        List<NoteDTO> notes = Arrays.asList(new NoteDTO("n1", "1", "Doe", "note"));
        ResponseEntity<List<NoteDTO>> notesResp = new ResponseEntity<>(notes, HttpStatus.OK);
        when(restTemplate.exchange(contains("/api/notes/patient/"), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(notesResp);

        ResponseEntity<?> response = controller.getPatientById(null, 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PatientDTO body = (PatientDTO) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getNotes()).hasSize(1);
    }

    @Test
    void getPatientById_nullPatientBody_returnsEmptyWithNotes() {
        ResponseEntity<PatientDTO> patientResp = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(contains("/api/patient/details/"), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(patientResp);

        ResponseEntity<List<NoteDTO>> notesResp = new ResponseEntity<>(List.of(), HttpStatus.OK);
        when(restTemplate.exchange(contains("/api/notes/patient/"), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(notesResp);

        ResponseEntity<?> response = controller.getPatientById(null, 2L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PatientDTO body = (PatientDTO) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getNotes()).isEmpty();
    }

    @Test
    void getPatientById_notesNon2xx_setsEmptyNotes() {
        PatientDTO dto = new PatientDTO(1L, "Doe", "Jane", "1990-09-05", Gender.F, " ", " ", null);
        ResponseEntity<PatientDTO> patientResp = new ResponseEntity<>(dto, HttpStatus.OK);
        when(restTemplate.exchange(contains("/api/patient/details/"), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(patientResp);

        ResponseEntity<List<NoteDTO>> notesResp = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(contains("/api/notes/patient/"), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(notesResp);

        ResponseEntity<?> response = controller.getPatientById(null, 3L);
        PatientDTO body = (PatientDTO) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getNotes()).isEmpty();
    }

    @Test
    void getPatientById_forbidden_returns403() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden"));
        ResponseEntity<?> response = controller.getPatientById(null, 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getPatientById_exception_returns500() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("boom"));
        ResponseEntity<?> response = controller.getPatientById(null, 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void addPatient_normalizesDateAndPhone_andForwards() {
        PatientDTO input = new PatientDTO(0, "Doe", "John", "05/09/1990", Gender.M, " ", "123 456 7890", null);
        PatientDTO returned = new PatientDTO(10, "Doe", "John", "1990-09-05", Gender.M, " ", "123-456-7890", null);
        ResponseEntity<PatientDTO> resp = new ResponseEntity<>(returned, HttpStatus.CREATED);
        when(restTemplate.exchange(contains("/api/patient/addpatient"), eq(HttpMethod.POST), any(HttpEntity.class), eq(PatientDTO.class)))
                .thenReturn(resp);

        ResponseEntity<?> response = controller.addPatient(null, input);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ArgumentCaptor<HttpEntity<PatientDTO>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(contains("/api/patient/addpatient"), eq(HttpMethod.POST), captor.capture(), eq(PatientDTO.class));
        PatientDTO sent = captor.getValue().getBody();
        assertThat(sent.getBirthdate()).isEqualTo("1990-09-05");
        assertThat(sent.getPhoneNumber()).isEqualTo("123-456-7890");
    }

    @Test
    void updatePatient_normalizesDateAndPhone_andForwards() {
        PatientDTO input = new PatientDTO(0, "Doe", "John", "05-09-1990", Gender.M, " ", "123/456/7890", null);
        PatientDTO returned = new PatientDTO(10, "Doe", "John", "1990-09-05", Gender.M, " ", "123-456-7890", null);
        ResponseEntity<PatientDTO> resp = new ResponseEntity<>(returned, HttpStatus.OK);
        when(restTemplate.exchange(contains("/api/patient/update/"), eq(HttpMethod.POST), any(HttpEntity.class), eq(PatientDTO.class)))
                .thenReturn(resp);

        ResponseEntity<?> response = controller.updatePatient(null, 10L, input);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<HttpEntity<PatientDTO>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(contains("/api/patient/update/"), eq(HttpMethod.POST), captor.capture(), eq(PatientDTO.class));
        PatientDTO sent = captor.getValue().getBody();
        assertThat(sent.getBirthdate()).isEqualTo("1990-09-05");
        assertThat(sent.getPhoneNumber()).isEqualTo("123-456-7890");
    }

    @Test
    void addPatient_forbidden_returns403() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(PatientDTO.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden"));
        ResponseEntity<?> response = controller.addPatient(null, new PatientDTO());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void updatePatient_exception_returns500() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(PatientDTO.class)))
                .thenThrow(new RuntimeException("boom"));
        ResponseEntity<?> response = controller.updatePatient(null, 1L, new PatientDTO());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
