package com.medilabo_gui.medilabo_gui.interceptor;

import com.medilabo_gui.medilabo_gui.services.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class JwtInterceptor implements ClientHttpRequestInterceptor {

//VERSION DE BASE
    private JwtTokenService jwtTokenService;

    public JwtInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
        System.out.println("Je passe dans le constructeur de JwtInterceptor" + this.jwtTokenService);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        System.out.println("je passe dans la methode intercept de la classe JwtInterceptor");
        String token = jwtTokenService.getJwtToken();
        System.out.println( "le token dans la methode intercept de la classe JwtInterceptor est : " +jwtTokenService.getJwtToken());
        if (token != null) {
            System.out.println("mon token est : " +token);
            request.getHeaders().set("Authorization", "Bearer " + token);
        }
        return execution.execute(request, body);
    }
}