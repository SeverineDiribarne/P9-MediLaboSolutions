package com.medilabo_gui.medilabo_gui.services.noteservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo_gui.medilabo_gui.model.Note;
import com.medilabo_gui.medilabo_gui.model.NoteForm;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService implements INoteService {

    private static final String GATEWAY_BASE_URL = "https://localhost:8090"; // Gateway
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(NoteService.class);

    @Override
    public List<Note> getNotesByPatientId(Long patientId, String jwtToken) {
        HttpHeaders headers = buildHeaders(jwtToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    GATEWAY_BASE_URL + "/api/notes/patient/" + patientId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<String>() {});
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), new TypeReference<List<Note>>() {});
            }
        } catch (Exception ex) {
            logger.error("Erreur lors de la récupération des notes du patient {}", patientId, ex);
        }
        return new ArrayList<>();
    }

    @Override
    public Note addNote(NoteForm form, String jwtToken) {
        HttpHeaders headers = buildHeaders(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            String json = objectMapper.writeValueAsString(form);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    GATEWAY_BASE_URL + "/api/notes",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<String>() {});
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), new TypeReference<Note>() {});
            }
        } catch (Exception ex) {
            logger.error("Erreur lors de l'ajout d'une note pour patient {}", form.getPatientId(), ex);
        }
        return null;
    }

    private HttpHeaders buildHeaders(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + jwtToken);
        return headers;
    }
}
