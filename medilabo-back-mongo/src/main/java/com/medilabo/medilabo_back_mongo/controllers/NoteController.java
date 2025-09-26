package com.medilabo.medilabo_back_mongo.controllers;

import com.medilabo.medilabo_back_mongo.dto.NoteRequest;
import com.medilabo.medilabo_back_mongo.model.Note;
import com.medilabo.medilabo_back_mongo.services.INoteService;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "http://localhost:8090")
public class NoteController {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NoteController.class);
   // private static final String LOG_ERROR = "The note could not be validated or registered in the database because the note details were empty or partially empty";
   // private static final String NOTE_ADD = "note/add";
   // private static final String NOTE_UPDATE = "note/update";
   // private static final String REDIRECT_NOTE_LIST = "redirect:/note/list";

    private final INoteService noteService;

    public NoteController(INoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Note>> getNotes(@PathVariable("patientId") String patientId) {
        log.info("Request to get notes for patient with ID: {}", patientId);
        return ResponseEntity.ok(noteService.getNotes(patientId));
    }

    @PostMapping
    public ResponseEntity<Note> addNote(@Valid @RequestBody NoteRequest noteRequest) {
        Note noteSaved = noteService.addNote(noteRequest.getPatientId(), noteRequest.getPatientLastname(),
                noteRequest.getNote());
        return ResponseEntity.ok(noteSaved);
    }
}