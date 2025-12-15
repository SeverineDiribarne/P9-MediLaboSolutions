package com.medilabo.medilabo_back_risk.dto;

import java.util.List;

import com.medilabo.medilabo_back_risk.model.Note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotesDTO {

    private List<Note> notes;

}
