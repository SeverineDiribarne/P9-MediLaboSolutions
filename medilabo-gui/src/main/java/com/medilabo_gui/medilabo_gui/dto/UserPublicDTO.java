package com.medilabo_gui.medilabo_gui.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicDTO {

    private String username;
    private List<String> authorities;
}
