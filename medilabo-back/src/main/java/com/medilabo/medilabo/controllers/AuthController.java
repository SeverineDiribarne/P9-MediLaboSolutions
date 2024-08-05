package com.medilabo.medilabo.controllers;

import com.medilabo.medilabo.security.AuthenticationRequest;
import com.medilabo.medilabo.security.AuthenticationResponse;
import com.medilabo.medilabo.security.CustomUserDetailsService;
import com.medilabo.medilabo.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;


    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:8090")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
         System.out.println("je passe dans la methode login du authController");
         try {
             authenticationManager.authenticate(
                     new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
             );
         } catch(BadCredentialsException e){
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
         }
         final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
         final String jwt = jwtTokenUtil.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("jwt", jwt);
        return ResponseEntity.ok(response);
     }
}
