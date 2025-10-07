package com.medilabo.medilabo_gateway.security;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class JwtBlackList {
    private final Set<String> blackListedTokens = ConcurrentHashMap.newKeySet();
    
    public void blackList(String token){
        blackListedTokens.add(token);
    }

    public boolean isBlackListed(String token){
        return blackListedTokens.contains(token);
    }
}
