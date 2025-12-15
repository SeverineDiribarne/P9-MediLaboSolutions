package com.medilabo_gui.medilabo_gui.model;

import jakarta.persistence.Id;
import lombok.*;

/**
* Represents a medical note stored in MongoDB (service notes).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    private String id;          
    private Long patientId; 
    private String patientLastname;   
    private String note;     
}
