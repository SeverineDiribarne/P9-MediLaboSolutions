package com.medilabo_gui.medilabo_gui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.medilabo_gui.medilabo_gui.model.User;


@Controller
@CrossOrigin(origins = "https://localhost:8090")
public class LoginController {

    @RequestMapping(value ="/login", method=RequestMethod.GET)
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

        @RequestMapping(value = "/login", method = RequestMethod.POST)
        public String performLogin(User user, Model model) {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String gatewayUrl = "https://localhost:8090/authentication";
            try {
                org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(gatewayUrl, user, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    // Authentification réussie, rediriger vers la page souhaitée
                    return "redirect:/list";
                }
            } catch (Exception e) {
                // Authentification échouée
                model.addAttribute("error", true);
                return "login";
            }
            model.addAttribute("error", true);
            return "login";
        }
}
