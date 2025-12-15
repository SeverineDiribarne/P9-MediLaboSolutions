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
        List<Note> notes = noteRepository.findByPatientId(patientId);
        return notes;
    }

    @Override
    public Note addNote(String patientId, String patientLastname, String note) {
        Note newNote = new Note();
        newNote.setPatientId(String.valueOf(patientId));
        newNote.setPatientLastname(patientLastname);
        newNote.setNote(note == null ? null : note.trim());
        return noteRepository.save(newNote);
    }

    @Override
    public void deleteNote(String id) {
        noteRepository.deleteById(id);
    }

    @Override
    public Note updateNote(String id, String note) {
        // Fetch existing (throws if not found)
        Note existing = noteRepository.findById(id).orElseThrow();
        // Simple validation (optional): do not accept an empty note
        if (note == null || note.trim().isEmpty()) {
            return existing; // Return existing unchanged (or throw an exception if preferred)
        }
        // Immutable reconstruction via AllArgs constructor (id preserved for update)
        Note updated = new Note(
                existing.getId(),
                existing.getPatientId(),
                existing.getPatientLastname(),
                note.trim()
        );
        return noteRepository.save(updated);
    }
}
