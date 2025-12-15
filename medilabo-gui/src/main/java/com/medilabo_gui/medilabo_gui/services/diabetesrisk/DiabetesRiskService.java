package com.medilabo_gui.medilabo_gui.services.diabetesrisk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo_gui.medilabo_gui.model.DiabetesRisk;

import io.micrometer.common.lang.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiabetesRiskService implements IDiabetesRisk {

    @Value("${gateway.url:https://localhost:8090}")
    private String gatewayBaseUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(DiabetesRiskService.class);

    public DiabetesRiskService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders buildHeaders(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    @Override
    public ResponseEntity<DiabetesRisk> getPatientDiabetesRisk(String id, String jwtToken) {
        HttpHeaders headers = buildHeaders(jwtToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
        ResponseEntity<String> response = restTemplate.exchange(
            gatewayBaseUrl + "/api/risk/" + id,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<String>() {}
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                DiabetesRisk risk = objectMapper.readValue(response.getBody(), new TypeReference<DiabetesRisk>() {});
                return new ResponseEntity<>(risk, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, response.getStatusCode());
        } catch (Exception ex) {
            logger.error("Erreur lors de la récupération du risque de diabète pour patient {}", id, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
