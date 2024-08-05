package com.medilabo.medilabo_back_mongo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private final SecretKey secretKey;

    public JwtTokenUtil(@Value("${jwt.secret}") String secret) {
        if (isValidBase64(secret)) {
            byte[] decodedKey = Base64.getDecoder().decode(secret);
            this.secretKey = Keys.hmacShaKeyFor(decodedKey);
            System.out.println("Loaded secret key from configuration");
        } else {
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // or HS384, HS512
            System.out.println("Generated new secret key: " + Base64.getEncoder().encodeToString(this.secretKey.getEncoded()));
        }
    }

    private boolean isValidBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Key getSigningKey() {
        System.out.println("je passe dans la methode getSigningKey du JwtTokenUtil");
        return this.secretKey;
    }

    public String extractUsername(String token){
        System.out.println("je passe dans la methode extractUsername du JwtTokenUtil");
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        System.out.println("je passe dans la methode extractClaim du JwtTokenUtil");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

    private Claims extractAllClaims(String token) {
        System.out.println("je passe dans la methode extractAllClaims du JwtTokenUtil");
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date extractExpiration(String token){
        System.out.println("je passe dans la methode extractExpiration du JwtTokenUtil");
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token){
        System.out.println("je passe dans la methode isTokenExpired du JwtTokenUtil");
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        System.out.println("je passe dans la methode generateToken du JwtTokenUtil");
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        System.out.println("je passe dans la methode createToken du JwtTokenUtil");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 *60 *60 *10))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        System.out.println("je passe dans la methode validateToken du JwtTokenUtil");
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

