package com.medilabo.medilabo_gateway.controllers;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.medilabo_gateway.dto.NoteDTO;
import com.medilabo.medilabo_gateway.dto.PatientDTO;
import com.medilabo.medilabo_gateway.session.SessionStore;
import org.springframework.web.bind.annotation.*;

@RestController
@SuppressWarnings({ "null" })
@RequestMapping("/api/patient")
@CrossOrigin
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    private static final String MEDILABO_BACK_BASE_URL = System.getenv().getOrDefault("BACK_URL",
            "https://medilabo-back:8082");
    private static final String MEDILABO_BACK_MONGO_BASE_URL = System.getenv().getOrDefault("MONGO_URL",
            "https://medilabo-back-mongo:8083");

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders getHeaders(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }
        String sessionNumber = token != null ? sessionStore.getSessionNumberByToken(token) : null;
        String username = sessionNumber != null ? sessionStore.getUsernameBySessionNumber(sessionNumber) : null;
        if (username != null) {
            headers.add("User-Name", username);
        }
        if (sessionNumber != null) {
            headers.add("Session-Number", sessionNumber);
        }
        return headers;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUsers(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String apiUrl = MEDILABO_BACK_BASE_URL + "/api/patient/list";
        HttpHeaders headers = getHeaders(authorization);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            List<PatientDTO> patients = Collections.emptyList();
            if (response.getBody() != null) {
                patients = objectMapper.readValue(
                        response.getBody(),
                        new TypeReference<List<PatientDTO>>() {
                        });
            }
            return ResponseEntity.ok(patients);
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("403 Forbidden from downstream service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden : " + e.getMessage());
        } catch (HttpClientErrorException e) {
            logger.error("Error from downstream service: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error parsing user list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing user list");
        }
    }

    @PostMapping("/addpatient")
    public ResponseEntity<?> addPatient(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody PatientDTO patient) {
        // Standardizing the date format before saving
        String inputDate = patient.getBirthdate();
        if (inputDate != null && !inputDate.isEmpty()) {
            // Replaces dots or slashes with dashes
            String normalized = inputDate.replace('.', '-').replace('/', '-');
            // Checks the dd-MM-yyyy format
            if (normalized.matches("\\d{2}-\\d{2}-\\d{4}")) {
                String[] parts = normalized.split("-");
                normalized = parts[2] + "-" + parts[1] + "-" + parts[0];
                patient.setBirthdate(normalized);
            }
        }
        // Standardization of the telephone number format before registration
        String inputPhone = patient.getPhoneNumber();
        if (inputPhone != null && !inputPhone.isEmpty()) {
            // Replaces dots, slashes and spaces with dashes

            String normalizedPhone = inputPhone.replace('.', '-').replace('/', '-').replace(' ', '-');
            // Checks the xxx-xxx-xxxx format

            if (normalizedPhone.matches("\\d{3}-\\d{3}-\\d{4}")) {
                patient.setPhoneNumber(normalizedPhone);
            }
        }
        String apiUrl = MEDILABO_BACK_BASE_URL + "/api/patient/addpatient";
        HttpHeaders headers = getHeaders(authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PatientDTO> entity = new HttpEntity<>(patient, headers);
        try {
            ResponseEntity<PatientDTO> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity,
                    PatientDTO.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("403 Forbidden from downstream service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden : " + e.getMessage());
        } catch (HttpClientErrorException e) {
            logger.error("Error from downstream service: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error parsing user list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing user list");
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getPatientById(
            @RequestHeader(value = "Authorization", required = false) String authorization, @PathVariable Long id) {
        String apiUrlPatientDetails = MEDILABO_BACK_BASE_URL + "/api/patient/details/" + id;
        HttpHeaders headers = getHeaders(authorization);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        // for medilabo-back part

        try {
            ResponseEntity<PatientDTO> responsePatientDetails = restTemplate.exchange(
                    apiUrlPatientDetails,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<PatientDTO>() {
                    });

            PatientDTO patient = new PatientDTO();
            if (responsePatientDetails.getStatusCode().is2xxSuccessful() && responsePatientDetails.getBody() != null) {
                patient = responsePatientDetails.getBody();
                logger.info("Patient details retrieved via gateway for id {}", id);
            } else {
                logger.warn("Gateway call succeeded but body empty or status not OK for id {}", id);
                patient = new PatientDTO();
            }
            // for medilabo-back-mongo part
            String apiUrlNote = MEDILABO_BACK_MONGO_BASE_URL + "/api/notes/patient/" + id;
            ResponseEntity<List<NoteDTO>> responseNote = restTemplate.exchange(
                    apiUrlNote,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<NoteDTO>>() {
                    });
            if (responseNote.getStatusCode().is2xxSuccessful() && responseNote.getBody() != null) {
                patient.setNotes(responseNote.getBody());
                logger.info("Patient details retrieved via gateway for id {}", id);
            } else {
                logger.warn("Gateway call succeeded but body empty or status not OK for id {}", id);
                patient.setNotes(List.of());
            }
            return ResponseEntity.ok(patient);
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("403 Forbidden from downstream service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden : " + e.getMessage());
        } catch (HttpClientErrorException e) {
            logger.error("Error from downstream service: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error parsing patient details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing patient details");
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePatient(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id, @RequestBody PatientDTO patient) {
        // Standardizing the date format before saving
        String inputDate = patient.getBirthdate();
        if (inputDate != null && !inputDate.isEmpty()) {
            // Replaces dots or slashes with dashes
            String normalized = inputDate.replace('.', '-').replace('/', '-');
            // Checks the dd-MM-yyyy format
            if (normalized.matches("\\d{2}-\\d{2}-\\d{4}")) {
                String[] parts = normalized.split("-");
                normalized = parts[2] + "-" + parts[1] + "-" + parts[0];
                patient.setBirthdate(normalized);
            }
        }
        // Standardization of the telephone number format before registration
        String inputPhone = patient.getPhoneNumber();
        if (inputPhone != null && !inputPhone.isEmpty()) {
            // Replaces dots, slashes and spaces with dashes
            String normalizedPhone = inputPhone.replace('.', '-').replace('/', '-').replace(' ', '-');
            // Checks the xxx-xxx-xxxx format
            if (normalizedPhone.matches("\\d{3}-\\d{3}-\\d{4}")) {
                patient.setPhoneNumber(normalizedPhone);
            }
        }
        String apiUrl = MEDILABO_BACK_BASE_URL + "/api/patient/update/" + id;
        HttpHeaders headers = getHeaders(authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PatientDTO> entity = new HttpEntity<>(patient, headers);
        try {
            ResponseEntity<PatientDTO> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity,
                    PatientDTO.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("403 Forbidden from downstream service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden : " + e.getMessage());
        } catch (HttpClientErrorException e) {
            logger.error("Error from downstream service: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error parsing user list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing user list");
        }
    }
}
