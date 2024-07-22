package com.medilabo.medilabo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class SecretKeyGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int KEY_LENGTH = 50;// 50 bytes for 256-bit key

    @Value("${jwt.secret:default-secret-key}")
    private String defaultSecret;

    @Bean
    public String jwtSecret() {
        System.out.println("la cle est " + generateSecretKey(KEY_LENGTH));
        return generateSecretKey(KEY_LENGTH);
    }

    private String generateSecretKey(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder keyBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            keyBuilder.append(CHARACTERS.charAt(randomIndex));
        }

        String secretKey = keyBuilder.toString();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(secretKey.getBytes());
    }

}