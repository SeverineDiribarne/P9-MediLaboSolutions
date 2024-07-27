package com.medilabo_gui.medilabo_gui.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableWebSecurity
public class ConfigurationApplicationSecurity {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        System.out.println("je passe dans la methode authenticationManager du ConfigurationApplicationSecurity");

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("je passe dans la methode filterChain du ConfigurationApplicationSecurity");
        http
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        .requestMatchers("/login", "/authenticate").permitAll()
                                        .anyRequest().authenticated()
                )
                .formLogin(
                        form ->
                                form
                                        .loginPage("/login")
                                        .loginProcessingUrl("/authenticate")
                                        .defaultSuccessUrl("/api/patient/list", true)
                                        .failureUrl("/home?error=true")
                )
                .logout(
                        logout ->
                                logout
                                        .logoutSuccessUrl("/login")
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        System.out.println("je passe dans la methode userDetailsService du ConfigurationApplicationSecurity");

        List<UserDetails> users = new ArrayList<>();
        users.add(User.withUsername("sedi77.sd@gmail.com")
                .password("?SD29@ds!")
                .roles("USER")
                .build());
//        users.add(User.withUsername("user2@example.com")
//                .password(new BCryptPasswordEncoder().encode("password2"))
//                .roles("USER")
//                .build());

        return new InMemoryUserDetailsManager(users);
    }
}
