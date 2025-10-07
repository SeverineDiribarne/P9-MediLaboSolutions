package com.medilabo_gui.medilabo_gui.utils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtUtils {

     /**
     * Decodes the payload of a JWT and extracts the 'roles' claim (no signature
     * verification).
     * 
     * @param jwt the JWT token string
     * @return the roles claim, or null if not present
     */
    public static List<GrantedAuthority> decodeAuthorities(String jwt){
        try{
            String [] parts = jwt.split("\\.");
            if(parts.length <2){
                return Collections.emptyList();
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payloadJson, new TypeReference<Map<String, Object>>(){    
            });
            Object authoritiesObj = claims.getOrDefault("authorities",claims.get("roles"));
            if(authoritiesObj instanceof List<?>){
                List<?> rawList = (List<?>) authoritiesObj;
                List<String> result = new ArrayList<>();
                for(Object item : rawList){
                    if(item != null){
                        result.add(item.toString());
                    }
                }
                    return result.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
                    } else if(authoritiesObj instanceof String){
                    // Sometimes authorities/roles is a comma-separated string
                    String str = (String) authoritiesObj;
                    String [] arr = str.split(",");
                    List<String> result = new ArrayList<>();
                    for (String s : arr){
                        if(!s.isBlank()){
                            result.add(s.trim());
                        }
                    }
                    return result.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
                    }
                    return Collections.emptyList();
        }catch(Exception e){
            return Collections.emptyList();
        }
    }
}
