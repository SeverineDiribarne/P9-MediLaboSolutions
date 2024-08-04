package com.medilabo_gui.medilabo_gui.security;

import com.medilabo_gui.medilabo_gui.services.JwtTokenService;
import com.medilabo_gui.medilabo_gui.services.UserPasswordService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

class CustomHeaderFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final UserPasswordService userPasswordService;

    public CustomHeaderFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService, UserPasswordService userPasswordService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
        this.userPasswordService = userPasswordService;
        System.out.println("Je passe dans le constructeur de CustomHeaderFilter" + this.jwtTokenService);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Add custom header
        System.out.println("Je passe dans le CustomHeaderFilter");
        String token = jwtTokenService.getJwtToken();
        System.out.println( "le token dans la methode doFilterInternal de la classe CustomHeaderFilter est : " +jwtTokenService.getJwtToken());
        if (token != null) {
            System.out.println("mon token est : " +token);
            CustomHttpServletRequestWrapper requestWrapper = new CustomHttpServletRequestWrapper(request);
            requestWrapper.addHeader("Authorization", "Bearer " + token);
            //TODO : faire la partie decodage du username dans le token ici
            //Todo : voir pourquoi le userpasswordService.users.get ne fonctionne pas
            final MyMainUser mainUser = userPasswordService.users.get("toto@gmail.com");
            System.out.println(userPasswordService.users);
            //TODO : remplacer la methode deprecated ci-dessous
            UserDetails user = User.withDefaultPasswordEncoder()
                    .username("toto@gmail.com") //mainUser.getUsername()
                    .password("toto") //mainUser.getPassword()
                    .build();
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            //authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            String authorizationHeader = requestWrapper.getHeader("Authorization");
            System.out.println("Le header contient : " + authorizationHeader);
            filterChain.doFilter(requestWrapper, response);
        }
        else
        {
            filterChain.doFilter(request, response);
        }
    }
}