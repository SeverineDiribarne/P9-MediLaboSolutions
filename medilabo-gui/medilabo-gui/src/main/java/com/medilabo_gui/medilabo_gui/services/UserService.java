package com.medilabo_gui.medilabo_gui.services;

import com.medilabo_gui.medilabo_gui.security.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
        String authUrl = gatewayUrl + "/authenticate";
        RestTemplate restTemplate = new RestTemplate();

        try {
            System.out.println("je passe par le try de la methode authenticate de la classe UserService");
            ResponseEntity<String> response = restTemplate.postForEntity(authUrl, new LoginRequest(username, password), String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("je passe par le catch de la methode authenticate de la classe UserService");
            log.error("Error occurred during authentication", e);
            return false;
        }
    }
}
