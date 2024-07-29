//package com.medilabo_gui.medilabo_gui.security;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        // Logique à exécuter en cas de succès d'authentification
//        System.out.println("Authentification réussie pour l'utilisateur : " + authentication.getName());
//
//        // Rediriger l'utilisateur vers la page souhaitée
//        response.sendRedirect("/api/patient/list");
//    }
//}