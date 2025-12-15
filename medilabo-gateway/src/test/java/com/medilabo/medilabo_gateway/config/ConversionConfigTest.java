package com.medilabo.medilabo_gateway.config;

import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;

import static org.assertj.core.api.Assertions.*;

class ConversionConfigTest {

    @Test
    void conversionService_convertsClasspathSecretKey() {
        ConversionConfig cfg = new ConversionConfig();
        ConversionService service = cfg.conversionService("HmacSHA256");
        SecretKey key = service.convert("classpath:hmac256.key", SecretKey.class);
        assertThat(key).isNotNull();
        assertThat(key.getAlgorithm()).isEqualTo("HmacSHA256");
        assertThat(key.getEncoded()).isNotEmpty();
    }

    @Test
    void conversionService_missingResource_throws() {
        ConversionConfig cfg = new ConversionConfig();
        ConversionService service = cfg.conversionService("HmacSHA256");
    assertThatThrownBy(() -> service.convert("classpath:missing.key", SecretKey.class))
        .hasRootCauseInstanceOf(java.io.FileNotFoundException.class);
    }
}
