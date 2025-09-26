package com.medilabo.medilabo_back_mongo.dto;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {

    @Id
    private Long id;
    @NotBlank
    private String patientId;
    @NotBlank
    private String patientLastname;
    @NotBlank
    private String note;
}
