package com.medilabo.medilabo.controllers;

import com.medilabo.medilabo.security.AuthenticationRequest;
import com.medilabo.medilabo.security.AuthenticationResponse;
import com.medilabo.medilabo.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

     @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
         authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
         );
         final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
         final String jwt = jwtTokenUtil.generateToken(userDetails);

         return ResponseEntity.ok(new AuthenticationResponse(jwt));
     }
}
