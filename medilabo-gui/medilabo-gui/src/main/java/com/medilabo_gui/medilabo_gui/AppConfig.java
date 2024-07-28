package com.medilabo_gui.medilabo_gui;

import com.medilabo_gui.medilabo_gui.interceptor.JwtInterceptor;
import com.medilabo_gui.medilabo_gui.services.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class AppConfig {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new JwtInterceptor(jwtTokenService));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}

