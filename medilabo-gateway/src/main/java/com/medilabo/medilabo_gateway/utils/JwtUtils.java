package com.medilabo.medilabo_gateway.utils;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtUtils {

    public static List<String> decodeAuthorities(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length < 2) {
                return Collections.emptyList();
            }
            String payloadJSON = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payloadJSON, new TypeReference<Map<String, Object>>() {
            });
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof String rolesStr) {
                return List.of(rolesStr.split(","));
            } else if (rolesObj instanceof List<?> rolesList) {
                return rolesList.stream().map(Object::toString).toList();
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}