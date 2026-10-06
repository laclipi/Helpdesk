package com.laura.helpdesk.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Configuración de seguridad de la aplicación.
@Configuration
public class SecurityConfig {

    // Define cómo Spring Security debe proteger nuestras peticiones HTTP.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desactivamos CSRF temporalmente porque estamos trabajando con una API REST.
                .csrf(csrf -> csrf.disable())

                // Permitimos temporalmente nuestras peticiones.
                // Más adelante sustituiremos esto por autenticación JWT.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());

        return http.build();
    }
}