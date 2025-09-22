package com.medilabo.medilabo_gateway.controllers;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.medilabo_gateway.dto.PatientDTO;
import com.medilabo.medilabo_gateway.session.SessionStore;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

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
}
