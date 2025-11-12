package com.medilabo.medilabo_gateway.controllers;

import com.medilabo.medilabo_gateway.dto.DiabetesRiskDTO;
import com.medilabo.medilabo_gateway.session.SessionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked"})
class DiabetesRiskControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SessionStore sessionStore;

    @InjectMocks
    private DiabetesRiskController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(sessionStore.getSessionNumber(anyString())).thenReturn("session-123");
    }

    @Test
    void getDiabetesRisk_success() {
        DiabetesRiskDTO dto = new DiabetesRiskDTO("IN_DANGER");
        ResponseEntity<DiabetesRiskDTO> resp = new ResponseEntity<>(dto, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(resp);

        ResponseEntity<?> response = controller.getDiabetesRiskById(null, 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DiabetesRiskDTO body = (DiabetesRiskDTO) response.getBody();
        assertThat(body.getDiabetesRiskLevel()).isEqualTo("IN_DANGER");
    }

    @Test
    void getDiabetesRisk_nullBody_returnsEmptyDto() {
        ResponseEntity<DiabetesRiskDTO> resp = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(resp);

        ResponseEntity<?> response = controller.getDiabetesRiskById(null, 2L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DiabetesRiskDTO body = (DiabetesRiskDTO) response.getBody();
        assertThat(body.getDiabetesRiskLevel()).isNull();
    }

    @Test
    void getDiabetesRisk_forbidden_returns403() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden"));
        ResponseEntity<?> response = controller.getDiabetesRiskById(null, 3L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getDiabetesRisk_otherClientError_propagatesStatus() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));
        ResponseEntity<?> response = controller.getDiabetesRiskById(null, 3L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getDiabetesRisk_exception_returns500() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("boom"));
        ResponseEntity<?> response = controller.getDiabetesRiskById(null, 3L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
