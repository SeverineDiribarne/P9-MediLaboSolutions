package com.medilabo_gui.medilabo_gui.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.medilabo_gui.medilabo_gui.model.Patient;

import java.util.List;

import  org.springframework.http.HttpEntity;

@Service
public class PatientService implements IPatientService{

    private final RestTemplate restTemplate;
    private final JwtTokenService jwtTokenService;

    public PatientService(RestTemplate restTemplate, JwtTokenService jwtTokenService) {
        this.restTemplate = restTemplate;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public ResponseEntity<List<Patient>> getPatientList() {
        String gatewayUrl = "http://localhost:8090/api/patient/list";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenService.getJwtToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Patient>> responseEntity = restTemplate.exchange(
                gatewayUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Patient>>() {});

        System.out.println(responseEntity.getBody().toString());
        return responseEntity;
    }

    @Override
    public ResponseEntity<Patient> addPatient(Patient patient) {
        String gatewayUrl = "http://localhost:8090/api/patient/addvalidate";

        return restTemplate.postForEntity(gatewayUrl, patient, Patient.class);
    }

    @Override
    public ResponseEntity<Patient> getPatientDetails(long id) {
        String gatewayUrl = "http://localhost:8090/api/patient/details/" + id;

        return restTemplate.getForEntity(gatewayUrl, Patient.class);
    }


    @Override
    public ResponseEntity<Patient>  getPatientToUpdate(long id) {
        String gatewayUrl = "http://localhost:8090/api/patient/update/" + id;
        return restTemplate.getForEntity(gatewayUrl, Patient.class);
    }


}
