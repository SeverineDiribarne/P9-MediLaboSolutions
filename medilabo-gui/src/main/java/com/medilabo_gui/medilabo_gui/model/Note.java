package com.medilabo_gui.medilabo_gui.model;

import jakarta.persistence.Id;
import lombok.*;

/**
 * Représente une note médicale stockée dans MongoDB (service notes).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    private String id;          // identifiant Mongo (ObjectId sous forme String)
    private Long patientId; 
    private String patientLastname;    // lien vers le patient
    private String note;     // contenu de la note
}
