package com.medilabo.medilabo_gateway.session;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SessionStore {

    private final ConcurrentHashMap<String, String> sessionMap = new ConcurrentHashMap<>();

    public void put(String sessionNumber, String username) {
        sessionMap.put(sessionNumber, username);
    }


    public String getSessionNumber(String userName){
        return sessionMap.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(userName))
            .map(ConcurrentHashMap.Entry::getKey)
            .findFirst()
            .orElse(null);
    }

    public boolean isValid(String sessionNumber) {
        return sessionMap.containsKey(sessionNumber);
    }

    public void remove(String sessionNumber) {
        sessionMap.remove(sessionNumber);
    }
}
