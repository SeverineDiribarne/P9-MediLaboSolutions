package com.medilabo.medilabo.security;

import com.medilabo.medilabo.model.User;
import com.medilabo.medilabo.services.userService.IUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private IUserService customerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User customer = customerService.findByUsername(username);
        if (customer.getUsername().equals(username)) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(customer.getUsername())
                    .password(new BCryptPasswordEncoder().encode(customer.getPassword()))
                    // .roles("USER")
                    .build();
        }
        else{
            throw new UsernameNotFoundException("User is not found.");
        }
    }
}
