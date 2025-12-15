package com.medilabo_gui.medilabo_gui.controller;

import com.medilabo_gui.medilabo_gui.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@CrossOrigin // Autorise par défaut toutes origines; peut être restreint via config globale
public class LoginController {

    @Value("${gateway.url:https://localhost:8090}")
    private String gatewayBaseUrl;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String performLogin(User user, Model model) {
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        String authEndpoint = gatewayBaseUrl.endsWith("/") ? gatewayBaseUrl + "authentication" : gatewayBaseUrl + "/authentication";
        try {
            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(authEndpoint, user, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return "redirect:/list";
            }
        } catch (Exception e) {
            model.addAttribute("error", true);
            return "login";
        }
        model.addAttribute("error", true);
        return "login";
    }
}
