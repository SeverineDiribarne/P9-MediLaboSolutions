package com.medilabo_gui.medilabo_gui.controller;

import com.medilabo_gui.medilabo_gui.model.NoteForm;
import com.medilabo_gui.medilabo_gui.services.noteservice.INoteService;
import com.medilabo_gui.medilabo_gui.utils.JwtUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api/notes")
public class NoteController {

    private final Logger logger = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    private INoteService noteService;

    @PostMapping("/add")
    public String addNote(UsernamePasswordAuthenticationToken authentication,
            @Valid @ModelAttribute("noteForm") NoteForm form,
            BindingResult result,
            Model model) {
        String jwtToken = (String) authentication.getDetails();
        if (jwtToken == null || jwtToken.isEmpty()) {
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("authorities", Collections.emptyList());
            return "login";
        }

        List<GrantedAuthority> authorities = JwtUtils.decodeAuthorities(jwtToken);
        model.addAttribute("authorities", authorities);

        if (result.hasErrors()) {
            logger.warn("Erreur de validation du formulaire de note: {}", result.getAllErrors());
            // On revient sur la page détails du patient
            return "redirect:/api/patient/details/" + form.getPatientId();
        }

        noteService.addNote(form, jwtToken);
        logger.info("Note ajoutée pour patient {}", form.getPatientId());
        return "redirect:/api/patient/details/" + form.getPatientId();
    }
}
