package com.medilabo.medilabo_back_risk.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class RestTemplateConfigTest {

    @Test
    @DisplayName("Le bean RestTemplate est créé sans exception")
    void restTemplateBeanCreated() throws Exception {
        RestTemplateConfig cfg = new RestTemplateConfig();
        RestTemplate rt = cfg.restTemplate();
        assertNotNull(rt);
    }
}
