package com.medilabo.medilabo.model;

import java.util.List;

public enum UserAuthorities {

    USER("USER"),
    ADMIN("ADMIN"),
    ADMIN_AND_USER("ADMIN","USER");

    private final List<String> authorities;

    UserAuthorities(String... authorities) {
        this.authorities = List.of(authorities);
    }

    public List<String> getAuthorities() {
        return authorities;
    }
}
