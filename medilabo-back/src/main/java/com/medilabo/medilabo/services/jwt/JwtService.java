package com.medilabo.medilabo.services.jwt;

import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    private static final long JWT_EXPIRATION_MS = Duration.ofHours(1).toMillis();

    public String generateSignedJwtToken(UserDetails userDetails) throws KeyLengthException {
        Date now = new Date();
        Date expiration = new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issuer("https:localhost:8083")
                .expirationTime(expiration)
                .issueTime(now)
                .claim("roles", userDetails.getAuthorities().toString())
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        MACSigner signer = new MACSigner(secretKey);

        try {
            signedJWT.sign(signer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign the JWT", e);
        }
        return signedJWT.serialize();
    }
}
