package com.medilabo.medilabo_back_mongo.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import com.medilabo.medilabo_back_mongo.dto.NoteRequest;
import com.medilabo.medilabo_back_mongo.model.Note;
import com.medilabo.medilabo_back_mongo.services.INoteService;
import org.springframework.web.bind.annotation.PutMapping;
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
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin
public class NoteController {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NoteController.class);
   

    private final INoteService noteService;

    private static final ConcurrentHashMap<String, AtomicInteger> requestCounters = new ConcurrentHashMap<>();

    public NoteController(INoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Note>> getNotes(@PathVariable("patientId") String patientId, HttpServletRequest request) {
        int count = requestCounters.computeIfAbsent(patientId == null ? "<null>" : patientId, k -> new AtomicInteger(0)).incrementAndGet();
        log.info("[NoteController] getNotes call #{} for patientId={} at {} from {}", count, patientId, Instant.now(), request != null ? request.getRemoteAddr() : "unknown");
        if (patientId == null || patientId.trim().isEmpty() || "0".equals(patientId)) {
            log.warn("[NoteController] patientId null, vide ou égal à 0 : {}", patientId);
            return ResponseEntity.badRequest().build();
        }
        List<Note> notes = noteService.getNotes(patientId);
        if (notes == null || notes.isEmpty()) {
            log.info("[NoteController] Aucune note trouvée pour patientId : {}. Retour 200 avec liste vide.", patientId);
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
        log.info("[NoteController] {} notes trouvées pour patientId : {}", notes.size(), patientId);
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<Note> addNote(@Valid @RequestBody NoteRequest noteRequest) {
        Note noteSaved = noteService.addNote(noteRequest.getPatientId(), noteRequest.getPatientLastname(),
                noteRequest.getNote());
        return ResponseEntity.ok(noteSaved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable("id") String id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable("id") String id, @Valid @RequestBody NoteRequest noteRequest) {
        Note updated = noteService.updateNote(id, noteRequest.getNote());
        return ResponseEntity.ok(updated);
    }
}