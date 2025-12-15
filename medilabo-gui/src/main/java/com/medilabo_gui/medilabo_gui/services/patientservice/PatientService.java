package com.medilabo_gui.medilabo_gui.services.patientservice;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.medilabo_gui.medilabo_gui.model.Patient;

import java.util.List;

@Service
public class PatientService implements IPatientService {

    private final RestTemplate restTemplate;

    public PatientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${gateway.url:https://localhost:8090}")
    private String gatewayBaseUrl;

      private HttpHeaders buildHeaders(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + jwtToken);
        return headers;
    }
     
    @Override
    public ResponseEntity<List<Patient>> getPatientList(String jwtToken) {
        String gatewayUrl = gatewayBaseUrl + "/api/patient/list";
        HttpHeaders headersRequest = buildHeaders(jwtToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headersRequest);

        return restTemplate.exchange(
                gatewayUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Patient>>() {
                });
    }

    @Override
    public ResponseEntity<Patient> addPatient(Patient patient, String jwtToken) {
        String gatewayUrl = gatewayBaseUrl + "/api/patient/addpatient";
         HttpHeaders headers = buildHeaders(jwtToken);

        HttpEntity<Patient> entity = new HttpEntity<>(patient, headers);

        return restTemplate.exchange(
                gatewayUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Patient>() {
                });
    }

    @Override
    public ResponseEntity<Patient> getPatientDetails(String id, String jwtToken) {
        String gatewayUrl = gatewayBaseUrl + "/api/patient/details/" + id;
        HttpHeaders headersRequest = buildHeaders(jwtToken);

        HttpEntity<String> entity = new HttpEntity<>(headersRequest);

        return restTemplate.exchange(
                gatewayUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Patient>() {
                });
    }

    @Override
    public ResponseEntity<Patient> getPatientToUpdateById(String patientId, String jwtToken) {
        String gatewayUrl = gatewayBaseUrl + "/api/patient/details/" + patientId;
        HttpHeaders headersRequest = buildHeaders(jwtToken);

        HttpEntity<String> entity = new HttpEntity<>(headersRequest);

        return restTemplate.exchange(
                gatewayUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Patient>() {
                });
    }

    @Override
    public ResponseEntity<Patient> updatePatient(String patientId, Patient patient, String jwtToken) {
    String gatewayUrl = gatewayBaseUrl + "/api/patient/update/" + patientId;
     HttpHeaders headers = buildHeaders(jwtToken);
    headers.setContentType(MediaType.APPLICATION_JSON);
    
    HttpEntity<Patient> entity = new HttpEntity<>(patient, headers);

    return restTemplate.exchange(
        gatewayUrl,
        HttpMethod.POST,
        entity,
        new ParameterizedTypeReference<Patient>() {
        });
    }
}