package com.medilabo.medilabo_gateway.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.medilabo.medilabo_gateway.dto.DiabetesRiskDTO;
import com.medilabo.medilabo_gateway.session.SessionStore;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DiabetesRiskController {

    private static final Logger logger = LoggerFactory.getLogger(DiabetesRiskController.class);
    private static final String MEDILABO_BACK_RISK_BASE_URL = System.getenv().getOrDefault("RISK_URL", "https://medilabo-back-risk:8084");

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SessionStore sessionStore;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        // TODO enlever toutes les adresses email en dur dans le code
        headers.add("User-Name", "toto@gmail.com");
        headers.add("Session-Number", sessionStore.getSessionNumber("toto@gmail.com"));
        return headers;
    }

    @GetMapping("/risk/{id}")
    public ResponseEntity<?> getDiabetesRiskById(
            @RequestHeader(value = "Authorization", required = false) String authorization, @PathVariable Long id) {
        String apiUrlPatientDiabetesRisk = MEDILABO_BACK_RISK_BASE_URL + "/api/risk/diabetes/" + id;
        HttpHeaders headers = getHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        // partie pour medilabo-back-risk
        try {
            ResponseEntity<DiabetesRiskDTO> responsePatientDiabetesRisk = restTemplate.exchange(
                    apiUrlPatientDiabetesRisk,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<DiabetesRiskDTO>() {
                    });

            DiabetesRiskDTO diabetesRisk = new DiabetesRiskDTO();
            if (responsePatientDiabetesRisk.getStatusCode().is2xxSuccessful()
                    && responsePatientDiabetesRisk.getBody() != null) {
                diabetesRisk = responsePatientDiabetesRisk.getBody();
                logger.info("Patient details retrieved via gateway for id {}", id);
            } else {
                logger.warn("Gateway call succeeded but body empty or status not OK for id {}", id);
                diabetesRisk = new DiabetesRiskDTO();
            }
            return ResponseEntity.ok(diabetesRisk);
        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("403 Forbidden from downstream service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden : " + e.getMessage());
        } catch (HttpClientErrorException e) {
            logger.error("Error from downstream service: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while calling diabetes risk service for id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while retrieving diabetes risk");
        }
    }
}
