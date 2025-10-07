package com.medilabo_gui.medilabo_gui.services.noteservice;

import com.medilabo_gui.medilabo_gui.model.Note;
import com.medilabo_gui.medilabo_gui.model.NoteForm;

import java.util.List;

public interface INoteService {

    List<Note> getNotesByPatientId(Long patientId, String jwtToken);

    Note addNote(NoteForm form, String jwtToken);
}
