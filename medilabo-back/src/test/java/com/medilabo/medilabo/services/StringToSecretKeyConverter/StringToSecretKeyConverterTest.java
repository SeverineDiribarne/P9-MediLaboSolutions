package com.medilabo.medilabo.services.StringToSecretKeyConverter;

import static org.junit.jupiter.api.Assertions.*;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringToSecretKeyConverterTest {

    @Test
    @DisplayName("convert retourne la clé par défaut quand la ressource est introuvable")
    void convert_returnsDefaultOnError() {
        StringToSecretKeyConverter c = new StringToSecretKeyConverter();
        SecretKey k = c.convert("classpath:missing.key");
        assertNotNull(k);
        assertEquals("HmacSHA256", k.getAlgorithm());
        assertTrue(k.getEncoded().length >= 16);
    }

    @Test
    @DisplayName("getDefaultSecretKey a un algo HmacSHA256")
    void defaultKey() {
        StringToSecretKeyConverter c = new StringToSecretKeyConverter();
        SecretKey k = c.getDefaultSecretKey();
        assertEquals("HmacSHA256", k.getAlgorithm());
        assertTrue(k.getEncoded().length > 0);
    }
}
