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
        System.out.println("Notes récupérées : " + notes);
        return notes;
        // Retour statique temporaire (bypass repository)
        // Note n1 = new Note();
        // n1.setId(1L);
        // n1.setPatientId(patientId);
        // n1.setPatientLastname("DummyLastname");
        // n1.setNote("Note de test 1 pour patient " + patientId);

        // Note n2 = new Note();
        // n2.setId(2L);
        // n2.setPatientId(patientId);
        // n2.setPatientLastname("DummyLastname");
        // n2.setNote("Note de test 2 pour patient " + patientId);

        // return java.util.List.of(n1, n2);
    }

    @Override
    public Note addNote(String patientId, String patientLastname, String note) {
        Note newNote = new Note();
        newNote.setPatientId(String.valueOf(patientId));
        newNote.setPatientLastname(patientLastname);
        newNote.setNote(note);
        return noteRepository.save(newNote);
    }

    @Override
    public void deleteNote(String id) {
        noteRepository.deleteById(id);
    }

    @Override
    public Note updateNote(String id, String note) {
        // Récupération de l'existant (exception si non trouvé)
        Note existing = noteRepository.findById(id).orElseThrow();
        // Validation simple (optionnelle) : ne pas accepter une note vide
        if (note == null || note.trim().isEmpty()) {
            return existing; // On retourne l'existant sans modification (ou lever une exception si souhaité)
        }
        // Reconstruction immuable via le constructeur AllArgs (id conservé pour update)
        Note updated = new Note(
                existing.getId(),
                existing.getPatientId(),
                existing.getPatientLastname(),
                note.trim()
        );
        return noteRepository.save(updated);
    }
}
