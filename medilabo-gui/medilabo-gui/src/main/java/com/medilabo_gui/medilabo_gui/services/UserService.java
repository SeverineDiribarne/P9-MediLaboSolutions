package com.medilabo_gui.medilabo_gui.services;

import com.medilabo_gui.medilabo_gui.security.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class UserService implements IUserService{

    //VERSION PRECEDENTE
//    @Value("${gateway.url}")
//    private String gatewayUrl;
//
//
//    private JwtTokenService jwtTokenService;
//
//    @Autowired
//    public UserService(JwtTokenService jwtTokenService) {
//        this.jwtTokenService = jwtTokenService;
//        System.out.println("Je passe dans le constructeur de UserService" + jwtTokenService);
//    }
//
//    public UserService(RestTemplate restTemplate) {
//    }
//
//    @Override
//    public boolean authenticate(String username, String password) {
//        System.out.println("je passe par la methode authenticate de la classe UserService");
//        String authUrl = gatewayUrl + "/api/auth/login";
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        try {
//            System.out.println("je passe par le try de la methode authenticate de la classe UserService");
//            System.out.println(authUrl);
//            // Créer le corps de la requête JSON
//            LoginRequest loginRequest = new LoginRequest(username, password);
//            System.out.println(loginRequest.toString());
//            // Configurer les en-têtes de la requête pour indiquer que le contenu est JSON
//            HttpHeaders headers = new HttpHeaders();
//            System.out.println(headers);
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            // Créer l'entité HTTP avec le corps de la requête et les en-têtes
//            HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);
//            System.out.println(request);
//            // Envoyer la requête POST
//            ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);
//            System.out.println(response);
//            if (response.getStatusCode().is2xxSuccessful()) {
//                // Extraire le token JWT de la réponse
//                Map<String, String> responseBody = response.getBody();
//                String jwt = responseBody.get("jwt");
//                // Faire quelque chose avec le token JWT si nécessaire
//                System.out.println("JWT Token: " + jwt);
//                jwtTokenService.setJwtToken(jwt);
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            System.out.println("je passe par le catch de la methode authenticate de la classe UserService");
//            log.error("Error occurred during authentication", e);
//            return false;
//        }
//    }
    @Value("${gateway.url}")
    private String gatewayUrl;

    private final RestTemplate restTemplate;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean authenticate(String username, String password) {
        System.out.println("je passe par la methode authenticate de la classe UserService");
        String authUrl = gatewayUrl + "/api/auth/login";

        try {
            System.out.println("je passe par le try de la methode authenticate de la classe UserService");
            System.out.println(authUrl);

            // Créer le corps de la requête JSON
            LoginRequest loginRequest = new LoginRequest(username, password);
            System.out.println(loginRequest.toString());

            //TODO voir comment faire pour que la methode JwtInterceptor se declenche apres la reception du jwtToken

            // Configurer les en-têtes de la requête pour indiquer que le contenu est JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Créer l'entité HTTP avec le corps de la requête et les en-têtes
            HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);
            System.out.println("la request est : " + request.toString());

            // Envoyer la requête POST
            ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);
            System.out.println("la response est : " + response.toString());

            if (response.getStatusCode().is2xxSuccessful()) {

                // Extraire le token JWT de la réponse
                Map<String, String> responseBody = response.getBody();
                String jwt = responseBody.get("jwt");

                // Faire quelque chose avec le token JWT si nécessaire
                System.out.println("JWT Token: " + jwt);
                jwtTokenService.setJwtToken(jwt);// Stocker le token JWT
                System.out.println( "le token que je viens de stocker est " + jwtTokenService.getJwtToken());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("je passe par le catch de la methode authenticate de la classe UserService");
            log.error("Error occurred during authentication", e);
            return false;
        }
    }
}

