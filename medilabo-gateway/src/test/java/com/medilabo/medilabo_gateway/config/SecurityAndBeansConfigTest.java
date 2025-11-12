package com.medilabo.medilabo_gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityAndBeansConfigTest {

    @Test
    void passwordEncoder_matchesAndNotMatches() {
        SecurityConfig cfg = new SecurityConfig();
        PasswordEncoder encoder = cfg.passwordEncoder();
        String raw = "secret";
        String encoded = encoder.encode(raw);
        assertThat(encoder.matches(raw, encoded)).isTrue();
        assertThat(encoder.matches("other", encoded)).isFalse();
    }
}
