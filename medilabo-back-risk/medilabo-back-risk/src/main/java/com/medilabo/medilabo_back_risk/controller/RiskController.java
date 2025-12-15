package com.medilabo.medilabo_back_risk.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.medilabo_back_risk.dto.DiabetesRiskDTO;
import com.medilabo.medilabo_back_risk.dto.NotesDTO;
import com.medilabo.medilabo_back_risk.dto.PatientDTO;
import com.medilabo.medilabo_back_risk.model.Note;
import com.medilabo.medilabo_back_risk.services.notesservice.NotesService;
import com.medilabo.medilabo_back_risk.services.patientservice.IPatientService;
import com.medilabo.medilabo_back_risk.services.riskservice.RiskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin
public class RiskController {

     private final Logger log = LoggerFactory.getLogger(RiskController.class);
     private static final String MEDILABO_BACK_BASE_URL = System.getenv().getOrDefault("BACK_URL", "https://medilabo-back:8082");
     private static final String MEDILABO_BACK_MONGO_BASE_URL = System.getenv().getOrDefault("MONGO_URL", "https://medilabo-back-mongo:8083");

     private final RestTemplate restTemplate;
     private final ObjectMapper objectMapper = new ObjectMapper();

     @Autowired
     private IPatientService patientService;

     @Autowired
     private NotesService notesService;

     @Autowired
     private RiskService riskService;

     public RiskController(RestTemplate restTemplate) {
          this.restTemplate = restTemplate;
     }

     @GetMapping("/diabetes/{id}")
     public ResponseEntity<?> getDiabetesRiskByPatientId(
               @PathVariable("id") Long patientId,
               @RequestHeader(value = "Authorization", required = false) String authorization,
               @RequestHeader(value = "User-Name", required = false) String userName,
               @RequestHeader(value = "Session-Number", required = false) String sessionNumber) {

          PatientDTO patientDTO = new PatientDTO();
          try {
               // 1) Retrieve patient details from medilabo-back (8082)
               patientDTO = getPatientDTO(patientId, authorization, userName, sessionNumber);
               if (patientDTO == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
               }
          } catch (HttpClientErrorException.NotFound e) {
               log.warn("Patient {} not found (404) from medilabo-back", patientId);
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
          } catch (HttpClientErrorException e) {
               log.error("Error while retrieving patient {} from medilabo-back - status: {}, body: {}", patientId,
                         e.getStatusCode(), e.getResponseBodyAsString());
               return ResponseEntity.status(e.getStatusCode()).body("Error retrieving patient details");
          } catch (Exception e) {
               log.error("Error while retrieving patient {} from medilabo-back: {}", patientId, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving patient details");
          }
          // 2) Retrieve patient notes from medilabo-back-mongo (8083)
          int triggerWordCount = 0;
          try {
               NotesDTO notesDTO = getNotesDTO(patientId, authorization, userName, sessionNumber);
               // send notes to NotesService for processing
               triggerWordCount = notesService.processNotesDTOData(notesDTO);
          } catch (HttpClientErrorException.NotFound ex) {
               // No notes for this patient -> continue with an empty list
               log.info("No notes found for patient {} in medilabo-back-mongo", patientId);
          } catch (HttpClientErrorException ex) {
               log.error("Error while retrieving notes for patient {} from medilabo-back-mongo - status: {}, body: {}",
                         patientId, ex.getStatusCode(), ex.getResponseBodyAsString());
               return ResponseEntity.status(ex.getStatusCode()).body("Error retrieving patient details");
          } catch (Exception ex) {
               log.error("Error while retrieving notes for patient {} from medilabo-back-mongo: {}", patientId, ex.getMessage(), ex);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving patient details");
          }
          String diabeteRiskLevel = "";
          diabeteRiskLevel = riskService.processRiskData(patientDTO, triggerWordCount);
          // 3) Compute/return risk (placeholder for now)
          DiabetesRiskDTO dto = new DiabetesRiskDTO(diabeteRiskLevel);
          return ResponseEntity.ok(dto);
     }

     private PatientDTO getPatientDTO(Long patientId, String authorization, String userName, String sessionNumber) throws Exception {
          // 1) Retrieve patient details from medilabo-back (8082)
          String patientUrl = MEDILABO_BACK_BASE_URL + "/api/patient/details/" + patientId;
          HttpHeaders headers = new HttpHeaders();
          if (authorization != null && !authorization.isBlank()) {
               headers.set("Authorization", authorization);
          }
          if (userName != null && !userName.isBlank()) {
               headers.set("User-Name", userName);
          }
          if (sessionNumber != null && !sessionNumber.isBlank()) {
               headers.set("Session-Number", sessionNumber);
          }
          ResponseEntity<String> patientResponse = restTemplate.exchange(
                    patientUrl,
                    HttpMethod.GET,
                    new HttpEntity<Void>(headers),
                    String.class);

          if (!patientResponse.getStatusCode().is2xxSuccessful() || patientResponse.getBody() == null) {
               log.warn("Patient {} not found or empty body from medilabo-back", patientId);
               return null;
          }

          // Optional: parse to verify existence / extract fields if needed
          Map<String, Object> patient = objectMapper.readValue(patientResponse.getBody(),
                    new TypeReference<Map<String, Object>>() {
                    });
          if (patient == null || patient.isEmpty()) {
               return null;
          }
          // send patient to PatientService for processing
          return patientService.processPatientDTOData(patient);
     }

     private NotesDTO getNotesDTO(Long patientId, String authorization, String userName, String sessionNumber) throws Exception {
          String notesUrl = MEDILABO_BACK_MONGO_BASE_URL + "/api/notes/patient/" + patientId;
          ResponseEntity<Note[]> notesResponse;
          NotesDTO notesDTO = new NotesDTO();
          HttpHeaders headers = new HttpHeaders();
          if (authorization != null && !authorization.isBlank()) {
               headers.set("Authorization", authorization);
          }
          if (userName != null && !userName.isBlank()) {
               headers.set("User-Name", userName);
          }
          if (sessionNumber != null && !sessionNumber.isBlank()) {
               headers.set("Session-Number", sessionNumber);
          }
          notesResponse = restTemplate.exchange(
                    notesUrl,
                    HttpMethod.GET,
                    new HttpEntity<Void>(headers),
                    Note[].class);

          List<Note> notes = notesResponse.getBody() != null ? Arrays.asList(notesResponse.getBody())
                    : Collections.emptyList();
          notesDTO = new NotesDTO(notes);
          log.debug("Retrieved {} notes for patient {}", notes.size(), patientId);

          return notesDTO;
     }
}
