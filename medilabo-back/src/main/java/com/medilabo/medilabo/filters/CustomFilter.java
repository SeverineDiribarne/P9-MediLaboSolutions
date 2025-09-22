package com.medilabo.medilabo.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.medilabo.medilabo.services.customUserDetailsService.CustomUserDetailsService;
import com.medilabo.medilabo.session.SessionStore;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomFilter  extends GenericFilterBean{

private final CustomUserDetailsService userDetailsService;

@Autowired
private SessionStore sessionStore;

CustomFilter(CustomUserDetailsService userDetailsService){
    this.userDetailsService = userDetailsService;
}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
       HttpServletRequest httpRequest = (HttpServletRequest) request;
       String usernameHeader = httpRequest.getHeader("User-Name");
       String sessionHeader = httpRequest.getHeader("Session-Number");
       String username = (usernameHeader != null && !usernameHeader.isEmpty()) ? usernameHeader : null;
       String sessionNumber = (sessionHeader != null && !sessionHeader.isEmpty()) ? sessionHeader : null;

       if(username != null && sessionNumber != null
       && sessionStore.isValid(sessionNumber)
       && sessionStore.getUsername(sessionNumber).compareTo(username) == 0 
       && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
          SecurityContextHolder.getContext().setAuthentication(authentication);  
        }
        filterChain.doFilter(request, response);
}
}
