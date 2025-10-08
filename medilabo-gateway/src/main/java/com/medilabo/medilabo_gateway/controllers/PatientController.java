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
@RequestMapping("/api")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/patient/list")
    public ResponseEntity<?> getUsers(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String apiUrl = "https://localhost:8082/api/patient/list";
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Name", "toto@gmail.com");
        headers.add("Session-Number", sessionStore.getSessionNumber("toto@gmail.com"));
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

    @PostMapping("/patient/addpatient")
    public ResponseEntity<?> addPatient(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody PatientDTO patient) {
        // Normalisation du format de date avant enregistrement
        String inputDate = patient.getBirthdate();
        if (inputDate != null && !inputDate.isEmpty()) {
            // Remplace les points ou slash par tirets
            String normalized = inputDate.replace('.', '-').replace('/', '-');
            // Vérifie le format dd-MM-yyyy
            if (normalized.matches("\\d{2}-\\d{2}-\\d{4}")) {
                String[] parts = normalized.split("-");
                normalized = parts[2] + "-" + parts[1] + "-" + parts[0];
                patient.setBirthdate(normalized);
            }
        }
        // Normalisation du format du numéro de téléphone avant enregistrement
        String inputPhone = patient.getPhoneNumber();
        if (inputPhone != null && !inputPhone.isEmpty()) {
            // Remplace points, slash et espaces par tirets
            String normalizedPhone = inputPhone.replace('.', '-').replace('/', '-').replace(' ', '-');
            // Vérifie le format xxx-xxx-xxxx
            if (normalizedPhone.matches("\\d{3}-\\d{3}-\\d{4}")) {
                patient.setPhoneNumber(normalizedPhone);
            }
        }
        String apiUrl = "https://localhost:8082/api/patient/addpatient";
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Name", "toto@gmail.com");
        headers.add("Session-Number", sessionStore.getSessionNumber("toto@gmail.com"));
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

    @GetMapping("/patient/details/{id}")
    public ResponseEntity<?> getPatientById(
            @RequestHeader(value = "Authorization", required = false) String authorization, @PathVariable Long id) {
        String apiUrlPatientDetails = "https://localhost:8082/api/patient/details/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Name", "toto@gmail.com");
        headers.add("Session-Number", sessionStore.getSessionNumber("toto@gmail.com"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        // partie pour medilabo-back
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
            // partie pour medilabo-back-mongo
            String apiUrlNote = "https://localhost:8083/api/notes/patient/" + id;
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
     @PostMapping("/patient/update/{id}")
    public ResponseEntity<?> updatePatient(@RequestHeader(value = "Authorization", required = false) String authorization,
     @PathVariable Long id, @RequestBody PatientDTO patient) {
        // Normalisation du format de date avant enregistrement
        String inputDate = patient.getBirthdate();
        if (inputDate != null && !inputDate.isEmpty()) {
            // Remplace les points ou slash par tirets
            String normalized = inputDate.replace('.', '-').replace('/', '-');
            // Vérifie le format dd-MM-yyyy
            if (normalized.matches("\\d{2}-\\d{2}-\\d{4}")) {
                String[] parts = normalized.split("-");
                normalized = parts[2] + "-" + parts[1] + "-" + parts[0];
                patient.setBirthdate(normalized);
            }
        }
        // Normalisation du format du numéro de téléphone avant enregistrement
        String inputPhone = patient.getPhoneNumber();
        if (inputPhone != null && !inputPhone.isEmpty()) {
            // Remplace points, slash et espaces par tirets
            String normalizedPhone = inputPhone.replace('.', '-').replace('/', '-').replace(' ', '-');
            // Vérifie le format xxx-xxx-xxxx
            if (normalizedPhone.matches("\\d{3}-\\d{3}-\\d{4}")) {
                patient.setPhoneNumber(normalizedPhone);
            }
        }
        String apiUrl = "https://localhost:8082/api/patient/update/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Name", "toto@gmail.com");
        headers.add("Session-Number", sessionStore.getSessionNumber("toto@gmail.com"));
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
