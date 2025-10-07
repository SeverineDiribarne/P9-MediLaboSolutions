package com.medilabo.medilabo_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {

    private String id;
    private String patientId;
    private String patientLastname;
    private String note;
}
