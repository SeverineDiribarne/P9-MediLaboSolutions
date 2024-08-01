package com.medilabo_gui.medilabo_gui.security;

import com.medilabo_gui.medilabo_gui.services.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

class MyMainUser implements UserDetails {

    private static final long serialVersionUID = 1L;
    private transient User user;

    public MyMainUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Map<String,Object> attributes()
    {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", user.getUsername());

        return attributes;
    }
}

class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final HashMap<String, String> customHeaders;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);
        if (headerValue != null) {
            return headerValue;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (customHeaders.containsKey(name)) {
            return Collections.enumeration(Collections.singletonList(customHeaders.get(name)));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(customHeaders.keySet());
    }
}

class CustomHeaderFilter extends OncePerRequestFilter {

    private JwtTokenService jwtTokenService;
    private UserDetailsService userDetailsService;

    @Autowired
    public CustomHeaderFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
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
            UserDetails user = User.withDefaultPasswordEncoder()
                    .username("toto@gmail.com")
                    .password("toto@TOTO")
                    .roles("USER")
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

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ConfigurationApplicationSecurity {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        System.out.println("je passe dans la methode authenticationManager du ConfigurationApplicationSecurity");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CustomHeaderFilter customHeaderFilter = new CustomHeaderFilter(jwtTokenService, null);

        System.out.println("je passe dans la methode filterChain du ConfigurationApplicationSecurity");
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/home","/login","/custom-login", "/error").permitAll()
                        .requestMatchers("/list","/details", "/add", "/update", "/logout", "/api/patient/list").authenticated()
                )
                .addFilterBefore(customHeaderFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/api/patient/list", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                        .invalidateHttpSession(true)
                );
        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        System.out.println("je passe dans la methode userDetailsService du ConfigurationApplicationSecurity");
        List<UserDetails> users = new ArrayList<>();
        users.add(User.withUsername("toto@gmail.com")
                .password(passwordEncoder().encode("toto@TOTO"))
                .roles("USER")
                .build());
        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
