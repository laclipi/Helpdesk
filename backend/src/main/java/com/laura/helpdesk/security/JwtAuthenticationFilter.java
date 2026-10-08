package com.laura.helpdesk.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Comprobamos si la petición contiene un Bearer Token.
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Extraemos el JWT que llega después de "Bearer ".
            String token = authHeader.substring(7);

            // Comprobamos temporalmente la estructura del JWT sin mostrar su contenido
            // completo.
            System.out.println(
                    "JWT length=" + token.length()
                            + ", dots=" + token.chars().filter(c -> c == '.').count());

            String email = jwtService.extractEmail(token);

            // Creamos una autenticación válida para el usuario identificado por el JWT.
            // No necesitamos permisos concretos todavía, pero sí una lista de authorities
            // vacía.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    java.util.Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
