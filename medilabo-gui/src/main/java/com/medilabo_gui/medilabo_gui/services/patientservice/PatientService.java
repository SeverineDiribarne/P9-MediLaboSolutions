package com.medilabo_gui.medilabo_gui.services.patientservice;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.medilabo_gui.medilabo_gui.model.Patient;

import java.util.Collections;
import java.util.List;

@Service
public class PatientService implements IPatientService {

    private final RestTemplate restTemplate;

    public PatientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<List<Patient>> getPatientList(String jwtToken) {
        String gatewayUrl = "https://localhost:8090/api/patient/list";

        HttpHeaders headersRequest = new HttpHeaders();
        headersRequest.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headersRequest.set("Authorization", "Bearer " + jwtToken);
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
        String gatewayUrl = "https://localhost:8090/api/patient/addpatient";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Patient> entity = new HttpEntity<>(patient, headers);

        return restTemplate.exchange(
                gatewayUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Patient>() {
                });
    }

    @Override
    public ResponseEntity<Patient> getPatientDetails(long id, String jwtToken) {
        String gatewayUrl = "https://localhost:8090/api/patient/details/" + id;

        HttpHeaders headersRequest = new HttpHeaders();
        headersRequest.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headersRequest.set("Authorization", "Bearer " + jwtToken);
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
        String gatewayUrl = "https://localhost:8090/api/patient/update/" + patientId;
        HttpHeaders headersRequest = new HttpHeaders();
        headersRequest.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headersRequest.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headersRequest);

        return restTemplate.exchange(
                gatewayUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Patient>() {
                });
    }
}