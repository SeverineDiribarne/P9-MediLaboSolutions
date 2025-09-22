package com.medilabo.medilabo.controllers;

import com.medilabo.medilabo.model.CustomUserDetails;
import com.medilabo.medilabo.services.jwt.JwtService;
import com.medilabo.medilabo.session.SessionStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class AuthenticationController {

    public record LoginRequest(String username, String password) {}

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/authentication")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest)
            throws RestClientException, Exception {

        try {

            Authentication authenticationRequest = UsernamePasswordAuthenticationToken
            .unauthenticated(
                loginRequest.username(), loginRequest.password());
            Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);

            SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

            if(authenticationResponse.isAuthenticated()){
                CustomUserDetails userDetails = (CustomUserDetails) authenticationResponse.getPrincipal();

                String jwtToken = jwtService.generateSignedJwtToken(userDetails);
                String sessionNumber = UUID.randomUUID().toString();

                sessionStore.put(sessionNumber, userDetails.getUsername());

                Map<String,String> data = new HashMap<String,String>();
                data.put("accessToken", jwtToken);
                data.put("sessionNumber", sessionNumber);

                return new ResponseEntity<>(data, HttpStatus.OK);
            }
            return new ResponseEntity<>("Authentication failed", HttpStatus.UNAUTHORIZED); 
        } catch(AuthenticationException e){
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }
}
