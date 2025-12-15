package com.medilabo.medilabo.dto;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserPublicDTO {

    private String username;
    private List<String> authorities;

    public UserPublicDTO(String username, List<String> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getAuthorities() {
        return authorities;
    }
}
