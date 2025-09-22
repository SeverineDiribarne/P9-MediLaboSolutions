package com.medilabo.medilabo_gateway.controllers;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import com.medilabo.medilabo_gateway.security.JwtBlackList;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtBlackList jwtBlackList;

    public record LoginRequest(String username, String password) {}

    public AuthenticationController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authentication")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest) throws RestClientException {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        if (authenticationResponse!=null && authenticationResponse.isAuthenticated() && authenticationResponse instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authenticationResponse;
            Map<String, String> data = new HashMap<>();
            data.put("accessToken", authenticationToken.getToken().getTokenValue());
            
            return new ResponseEntity<>(data, HttpStatus.OK);
        }
        return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtBlackList.blackList(token);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Invalid Authorization header");
        }
    }


}
