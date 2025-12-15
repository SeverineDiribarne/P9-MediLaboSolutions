package com.medilabo.medilabo_gateway.session;

public class SessionInfo {
    private final String sessionNumber;
    private final String username;
    private String token;

    public SessionInfo(String sessionNumber, String username, String token) {
        this.sessionNumber = sessionNumber;
        this.username = username;
        this.token = token;
    }

    public String getSessionNumber() {
        return sessionNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
