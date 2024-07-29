package com.medilabo_gui.medilabo_gui.security;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginRequest {

    String username;
    String password;
}
