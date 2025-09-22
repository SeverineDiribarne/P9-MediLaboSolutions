package com.medilabo.medilabo_back_mongo.controllers;

import com.medilabo.medilabo_back_mongo.model.Note;
import com.medilabo.medilabo_back_mongo.services.INoteService;
import com.medilabo.medilabo_back_mongo.services.IUserService;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8090")
public class NoteController {


    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NoteController.class);
    private static final String LOG_ERROR = "The note could not be validated or registered in the database because the note details were empty or partially empty";
    private static final String NOTE_ADD = "note/add";
    private static final String NOTE_UPDATE = "note/update";
    private static final String REDIRECT_NOTE_LIST = "redirect:/note/list";

    @Autowired
    private final INoteService noteService = null;
  //  @Autowired
  //  private final IUserService userService;

 @RequestMapping("/note/list")
    public Iterable<Note> home(Model model) {
        Iterable<Note> notes = noteService.getNoteList();
        log.info("all notes are found and returned to view");
        return notes;
    }

     @PostMapping("/patient/addvalidate")
    public String addNoteValidate(@Valid @RequestBody Note note, Model model, BindingResult bindingResult) {

    }

 @GetMapping("/note/details/{id}")
    public Optional<Note> detailsOfNote(@PathVariable long id) {
        // Logique pour récupérer les détails du patient en fonction de l'id
        Optional<Note> patientNote = noteService.getNoteById(id);
        log.info(" note is found and returned to view");
        return patientNote;

    }












}


