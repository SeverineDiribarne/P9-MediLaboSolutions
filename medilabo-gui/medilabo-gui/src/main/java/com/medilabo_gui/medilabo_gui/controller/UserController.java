package com.medilabo_gui.medilabo_gui.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class UserController {

    @GetMapping("/")
    public String home (Model model){
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "login";
    }
}
