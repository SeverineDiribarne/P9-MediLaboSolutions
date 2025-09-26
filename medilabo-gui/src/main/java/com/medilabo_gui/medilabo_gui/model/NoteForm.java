package com.medilabo_gui.medilabo_gui.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * Form backing object pour créer une note.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteForm {
    @NotNull
    private Long patientId;

    @NotBlank
    private String patientLastname;

    @NotBlank
    private String note;
}
