package com.medilabo_gui.medilabo_gui.services;

import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Service
public class JwtTokenService {

    private String jwtToken;

}

