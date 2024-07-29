package com.medilabo_gui.medilabo_gui.controller;

import com.medilabo_gui.medilabo_gui.model.User;
import com.medilabo_gui.medilabo_gui.services.IUserService;
import com.medilabo_gui.medilabo_gui.services.JwtTokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class UserController {

    private final IUserService userService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public UserController(IUserService userService, JwtTokenService jwtTokenService) {
        System.out.println("Je passe dans le constructeur du UserController" + jwtTokenService + " " + userService);
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @GetMapping("/home")
    public String home(){
        System.out.println("je passe par la methode home du userController");
        return "home";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model){
        System.out.println("je passe par la methode login du userController");
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/custom-login")
    public String login(@ModelAttribute("user") User user, BindingResult result, Model model) {
        System.out.println("je passe par la methode authenticate du userController");
            if (userService.authenticate(user.getUsername(), user.getPassword())) {
                System.out.println("le token sauvegarde est " +jwtTokenService.getJwtToken());
                return "/list";
            } else {
                model.addAttribute("error", "Invalid username or password");
                model.addAttribute("user", user);
                return "login";
            }
    }
}
