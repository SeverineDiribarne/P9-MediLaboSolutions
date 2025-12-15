package com.medilabo.medilabo_gateway.session;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SessionStore {

    // Single HashMap storing sessionNumber -> SessionInfo
    private final ConcurrentHashMap<String, SessionInfo> sessionMap = new ConcurrentHashMap<>();

    public void put(String sessionNumber, String username) {
        sessionMap.put(sessionNumber, new SessionInfo(sessionNumber, username, null));
    }


    public String getSessionNumber(String userName){
        return sessionMap.entrySet()
            .stream()
            .filter(entry -> userName.equals(entry.getValue().getUsername()))
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

    // --- Token association stored inside SessionInfo ---
    public void attachTokenToSession(String sessionNumber, String token) {
        sessionMap.computeIfPresent(sessionNumber, (sn, info) -> {
            info.setToken(token);
            return info;
        });
    }

    public String getSessionNumberByToken(String token) {
        return sessionMap.entrySet()
            .stream()
            .filter(e -> token != null && token.equals(e.getValue().getToken()))
            .map(ConcurrentHashMap.Entry::getKey)
            .findFirst()
            .orElse(null);
    }

    public boolean isValidToken(String token) {
        return getSessionNumberByToken(token) != null;
    }

    public String getUsernameBySessionNumber(String sessionNumber) {
        SessionInfo info = sessionMap.get(sessionNumber);
        return info != null ? info.getUsername() : null;
    }
}
