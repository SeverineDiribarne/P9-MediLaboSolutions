package com.medilabo.medilabo_gateway.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@ConfigurationPropertiesBinding
public class StringToSecretKeyConverter implements Converter<String, SecretKey> {

private static final Logger logger = LoggerFactory.getLogger(StringToSecretKeyConverter.class);

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    private String algorithm;

    @Override
    public SecretKey convert(@NonNull String classPathParameter) {
        String[] classPathParameters = classPathParameter.split(":");
        ClassPathResource classPathResource = new ClassPathResource(classPathParameters[1]);
        try {
            Path path = Path.of(classPathResource.getURI());
            byte[] secretKeyValue = Files.readAllBytes(path);
            return new SecretKeySpec(secretKeyValue, algorithm);
        } catch (IOException e) {
            logger.error("Failed to read secret key from resource: {}", classPathResource.getPath(), e);
            throw new IllegalStateException("Could not load secret key from resource: " + classPathResource.getPath(),
                    e);
        }
    }
}
