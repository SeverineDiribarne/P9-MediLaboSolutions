package com.medilabo.medilabo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

 

@Configuration
public class SecretKeyConfig {
    
    @Value("${hmac256.key}")
    private String classPathParameter;

    @Value("${algorithm:HmacSHA256}")
    private String algorithm;

    private static final Logger logger = LoggerFactory.getLogger(SecretKeyConfig.class);

    @Bean
    public SecretKey jwtSecretKey(){
        String path = Objects.requireNonNull(classPathParameter, "Property 'hmac256.key' must not be null");
        ClassPathResource classPathResource = new ClassPathResource(path);

        try (InputStream is = classPathResource.getInputStream()){
            byte[] secretKeyValue = is.readAllBytes();
            return new SecretKeySpec(secretKeyValue, algorithm);
        } catch(IOException e){
            logger.error("Failed to read secret key from resource: {}", classPathResource.getPath(), e);
            throw new IllegalStateException("Could not load secret key from resource: " + classPathResource.getPath(), e);
        }
    }
}
