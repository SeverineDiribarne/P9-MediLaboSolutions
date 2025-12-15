package com.medilabo.medilabo.services.StringToSecretKeyConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;


public class StringToSecretKeyConverter implements Converter<String, SecretKey>{

private static final Logger logger = LoggerFactory.getLogger(StringToSecretKeyConverter.class);

private String algorithm = "HmacSHA256";

    @Override
    public SecretKey convert(@org.springframework.lang.NonNull String classPathParameter) {
        String[] classPathParameters = Objects.requireNonNull(classPathParameter, "classPathParameter must not be null").split(":");
        String resourcePath = Objects.requireNonNull(classPathParameters.length > 1 ? classPathParameters[1] : null, "Missing resource path after ':'");
        ClassPathResource classPathResource = new ClassPathResource(resourcePath);

        try{
            Path path =Path.of(classPathResource.getURI());
            // Read the key from the file
            byte[] secretKeyValue = Files.readAllBytes(path);
        return new SecretKeySpec(secretKeyValue, algorithm); 
        } catch (IOException e) {
            logger.error("Error", e);
        }
        return getDefaultSecretKey();
        }

        public SecretKey getDefaultSecretKey(){
            String base64Key = "u8f4k2j3l9s8d7f6g5h4j3k2l1m0n9b8v7c6x5z4a3s2d1f0g9h8j7k6l5m4n3b2";
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
        }
}
