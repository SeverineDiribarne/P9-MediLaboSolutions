package com.medilabo_gui.medilabo_gui.services;

import com.medilabo_gui.medilabo_gui.security.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Slf4j
@Service
public class UserService implements IUserService{

    @Value("${gateway.url}")
    private String gatewayUrl;

    public UserService(RestTemplate restTemplate) {}

    @Override
    public boolean authenticate(String username, String password) {
        System.out.println("je passe par la methode authenticate de la classe UserService");
        String authUrl = gatewayUrl + "/api/login";
        RestTemplate restTemplate = new RestTemplate();

        try {
            System.out.println("je passe par le try de la methode authenticate de la classe UserService");

            // Créer le corps de la requête JSON
            LoginRequest loginRequest = new LoginRequest(username, password);

            // Configurer les en-têtes de la requête pour indiquer que le contenu est JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Créer l'entité HTTP avec le corps de la requête et les en-têtes
            HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

            // Envoyer la requête POST
            ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);

            // Vérifier si le statut de la réponse est 2xx (succès)
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("je passe par le catch de la methode authenticate de la classe UserService");
            log.error("Error occurred during authentication", e);
            return false;
        }
    }
}
