package com.medilabo.medilabo.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.medilabo.medilabo.services.StringToSecretKeyConverter.StringToSecretKeyConverter;

@Configuration
public class SecretKeyConfig {
    
    @Value("${hmac256.key}")
    private String classPathParameter;

    @Value("${algorithm:HmacSHA256}")
    private String algorithm;

    private static final Logger logger = LoggerFactory.getLogger(StringToSecretKeyConverter.class);

    @Bean
    public SecretKey jwtSecretKey(){
      //  String[] classPathParameters = classPathParameter.split(":");
        ClassPathResource classPathResource = new ClassPathResource(classPathParameter);

        try{
       //     Path path =Path.of(classPathResource.getURI());
            byte[] secretKeyValue = classPathResource.getInputStream().readAllBytes();
            return new SecretKeySpec(secretKeyValue, algorithm); 
        } catch(IOException e){
            logger.error("Failed to read secret key from resource: {}", classPathResource.getPath(), e);
            throw new IllegalStateException("Could not load secret key from resource: " +classPathResource.getPath(), e);
        }
    }
}
