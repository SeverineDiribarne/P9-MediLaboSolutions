package com.medilabo_gui.medilabo_gui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.medilabo_gui.medilabo_gui.providers.JwtAuthenticationProvider;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.lang.NonNull;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
//Freely allow static resources to prevent the browser from receiving the /login page instead (nosniff MIME error)
            .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
//Public pages
            .requestMatchers("/login", "/api/auth/login", "/").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successForwardUrl("/api/patient/list"))
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(
            @Value("${gui.cors.allowed-origins:https://localhost,https://localhost:8443}") String allowedOrigins) {
        return new WebMvcConfigurer() {
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                String[] origins = java.util.Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);
                registry.addMapping("/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(JwtAuthenticationProvider authenCustomProvider) {
        return new ProviderManager(authenCustomProvider);
    }
}
