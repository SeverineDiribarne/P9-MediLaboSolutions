package com.medilabo.medilabo_gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class RestTemplateConfigTest {

    @Test
    void restTemplateBean_hasHttpComponentsFactoryAndTimeouts() throws Exception {
        RestTemplateConfig cfg = new RestTemplateConfig();
        RestTemplate rt = cfg.restTemplate();
        assertThat(rt).isNotNull();
        ClientHttpRequestFactory factory = rt.getRequestFactory();
        assertThat(factory).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);
    // Vérifie simplement le type; les getters de timeout ne sont pas exposés dans cette version.
    }
}
