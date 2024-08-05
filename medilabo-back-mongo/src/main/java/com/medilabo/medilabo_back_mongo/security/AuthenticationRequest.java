package com.medilabo.medilabo_back_mongo.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {
    private String username;
    private String password;
}
