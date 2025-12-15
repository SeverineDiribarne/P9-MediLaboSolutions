package com.medilabo.medilabo.services.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jwt.SignedJWT;

class JwtServiceTest {

    private UserDetails sampleUser() {
        return new User("john@doe", "pwd", Collections.<GrantedAuthority>emptyList());
    }

    @Test
    @DisplayName("generateSignedJwtToken produit un JWT HS256 signé avec subject et roles")
    void generate_ok() throws Exception {
        byte[] keyBytes = new byte[32]; // 256 bits
        for (int i = 0; i < keyBytes.length; i++) keyBytes[i] = (byte) i;
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
        JwtService svc = new JwtService(key);

        String token = svc.generateSignedJwtToken(sampleUser());
        assertNotNull(token);

        SignedJWT parsed = SignedJWT.parse(token);
        assertEquals("HS256", parsed.getHeader().getAlgorithm().getName());
        assertEquals("john@doe", parsed.getJWTClaimsSet().getSubject());
        assertEquals("https:localhost:8083", parsed.getJWTClaimsSet().getIssuer());
        assertNotNull(parsed.getJWTClaimsSet().getExpirationTime());
        assertTrue(parsed.getJWTClaimsSet().getStringClaim("roles").contains("[]"));
    }

    @Test
    @DisplayName("generateSignedJwtToken lève KeyLengthException si clé invalide")
    void generate_badKey() {
        byte[] keyBytes = new byte[8]; // trop court
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
        JwtService svc = new JwtService(key);
        assertThrows(KeyLengthException.class, () -> svc.generateSignedJwtToken(sampleUser()));
    }
}
