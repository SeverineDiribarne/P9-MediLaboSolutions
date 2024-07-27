package com.medilabo.medilabo.security;

import com.fasterxml.jackson.core.Base64Variant;
import com.medilabo.medilabo.model.User;
import com.medilabo.medilabo.services.userService.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IUserService userService;

    @Autowired
    public CustomUserDetailsService(IUserService userService) {
        this.userService=userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("je passe dans la methode loadUserByUsername du CustomUserDetailsService");
        User user = userService.findByEmail(username);
        System.out.println("je passe apres le userService dans la methode loadUserByUsername du CustomUserDetailsService");
        if (user != null) {
            System.out.println("je passe dans le if de la methode loadUserByUsername du CustomUserDetailsService");
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    // .roles("USER")
                    .build();
        }
        else{
            System.out.println("je passe dans le else de la methode loadUserByUsername du CustomUserDetailsService");
            throw new UsernameNotFoundException("User is not found.");
        }
    }
}
