package com.medilabo_gui.medilabo_gui.interceptor;

import com.medilabo_gui.medilabo_gui.services.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtInterceptor implements ClientHttpRequestInterceptor {

    private JwtTokenService jwtTokenService;

    public JwtInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
        System.out.println("Je passe dans le constructeur de JwtInterceptor" + this.jwtTokenService);
    }

    //public JwtInterceptor() {
    //}

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        System.out.println("je passe dans la methode intercept de la classe JwtInterceptor");
        String token = jwtTokenService.getJwtToken();
        System.out.println(token);
        if (token != null) {
            request.getHeaders().set("Authorization", "Bearer " + token);
        }
        return execution.execute(request, body);
    }
}
