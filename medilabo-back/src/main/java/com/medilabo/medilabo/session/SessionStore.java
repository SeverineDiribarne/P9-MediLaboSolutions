package com.medilabo.medilabo.session;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SessionStore {

    private final ConcurrentHashMap<String, String> sessionMap = new ConcurrentHashMap<>();

    public void put(String sessionNumber, String username) {
        sessionMap.put(sessionNumber, username);
    }

    public String getUsername(String sessionNumber) {
        return sessionMap.get(sessionNumber);
    }

    public boolean isValid(String sessionNumber) {
        return sessionMap.containsKey(sessionNumber);
    }

    public void remove(String sessionNumber) {
        sessionMap.remove(sessionNumber);
    }

}
