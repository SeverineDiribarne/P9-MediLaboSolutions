package com.medilabo.medilabo_back_mongo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.POST;


@Configuration
@EnableWebSecurity
public class ConfigurationApplicationSecurity {

    private final JwtRequestFilter jwtFilter;

    @Autowired
    public ConfigurationApplicationSecurity(JwtRequestFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        System.out.println("je passe dans la methode authenticationManager du ConfigurationApplicationSecurity");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        System.out.println("je passe dans la methode filterChain du ConfigurationApplicationSecurity");
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // .csrfTokenRepository(csrfTokenRepository())
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(POST, "").permitAll()
                                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
//    @Bean
//    public CsrfTokenRepository csrfTokenRepository() {
//        CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();
//        repository.setCookieHttpOnly(false); // Allow JavaScript access to CSRF token
//        return repository;
//    }
}

