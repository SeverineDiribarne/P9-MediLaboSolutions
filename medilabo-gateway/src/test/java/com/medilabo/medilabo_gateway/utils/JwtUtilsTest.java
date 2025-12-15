package com.medilabo.medilabo_gateway.utils;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private String buildJwtWithPayload(String payloadJson){
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + ".";
    }

    @Test
    void decodeAuthorities_fromCommaSeparatedString() {
        String jwt = buildJwtWithPayload("{\"roles\":\"ROLE_USER,ROLE_ADMIN\"}");
        List<String> roles = JwtUtils.decodeAuthorities(jwt);
        assertThat(roles).containsExactly("ROLE_USER","ROLE_ADMIN");
    }

    @Test
    void decodeAuthorities_fromArray() {
        String jwt = buildJwtWithPayload("{\"roles\":[\"ROLE_USER\",\"ROLE_ADMIN\"]}");
        List<String> roles = JwtUtils.decodeAuthorities(jwt);
        assertThat(roles).containsExactly("ROLE_USER","ROLE_ADMIN");
    }

    @Test
    void decodeAuthorities_malformedJwt_returnsEmpty() {
        List<String> roles = JwtUtils.decodeAuthorities("malformed");
        assertThat(roles).isEmpty();
    }
}
