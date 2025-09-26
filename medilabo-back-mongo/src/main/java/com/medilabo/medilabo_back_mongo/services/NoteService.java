package com.medilabo.medilabo_back_mongo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medilabo.medilabo_back_mongo.model.Note;
import com.medilabo.medilabo_back_mongo.repositories.INoteRepository;

@Service
public class NoteService implements INoteService {

    private final INoteRepository noteRepository;

    public NoteService(INoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public List<Note> getNotes(String patientId) {
        return noteRepository.findByPatientId(patientId);
    }

    @Override
    public Note addNote(String patientId, String patientLastname, String note) {
        Note newNote = new Note();
        newNote.setPatientId(String.valueOf(patientId));
        newNote.setPatientLastname(patientLastname);
        newNote.setNote(note);
        return noteRepository.save(newNote);
    }
}
