package com.medilabo_gui.medilabo_gui.controller;

import com.medilabo_gui.medilabo_gui.services.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Controller
public class UserController {

    private final IUserService userService;

    @GetMapping("/home")
    public String home(){
        System.out.println("je passe par la methode home du userController");
        return "home";
    }

    @GetMapping("/login")
    public String login(){
        System.out.println("je passe par la methode login du userController");
        return "login";
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model) {
        System.out.println("je passe par la methode authenticate du userController");
            if (userService.authenticate(username, password)) {
                return "redirect:/list";
            } else {
                model.addAttribute("error", "Invalid username or password");
                return "login";
            }
    }
}
