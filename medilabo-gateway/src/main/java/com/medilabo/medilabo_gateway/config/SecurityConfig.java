package com.medilabo.medilabo_gateway.config;

import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/authentication").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer((oauth2ResourceServer) -> oauth2ResourceServer
                        .jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            MyCustomAuthenticationProvider authenCustomProvider) {
        return new ProviderManager(authenCustomProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            @Value("${back.url:https://localhost:8082}") String backUrl,
            @Value("${mongo.url:https://localhost:8083}") String mongoUrl) {
        return builder.routes()
                .route("dedude_response_header_route", r -> r
                        .path("/authentication")
                        .filters(f -> f.dedupeResponseHeader(
                                "Access-Control-Allow-Credentials",
                                "RETAIN_FIRST"))
                        .uri(backUrl))

                .route("backend_route", r -> r
                        .path("/api/patient/list")
                        .uri(backUrl))

                .route("medilabo-addpatientbyid", r -> r
                        .path("/api/patient/details/{id}")
                        .uri(backUrl))

                .route("medilabo-details-getnotes", r -> r
                        .path("/api/notes/patient/{patientid}")
                        .filters(f -> f.addRequestParameter("patientid", "defaultId"))
                        .uri(mongoUrl))

                .route("medilabo-addpatient", r -> r
                         .path("/api/notes/patient/addnote")
                         .uri(mongoUrl))
                .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(@Value("${hmac256.key}") SecretKey secretKey) {
        return NimbusReactiveJwtDecoder.withSecretKey(new SecretKeySpec(secretKey.getEncoded(), "HmacSHA256")).build();
    }
}