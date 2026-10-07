package com.laura.helpdesk.security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Service encargado de generar y gestionar tokens JWT.
@Service
public class JwtService {

    // Spring obtiene este valor desde application.properties.
    @Value("${jwt.secret}")
    private String secret;

    // Convierte el secreto de configuración en una clave criptográfica.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Genera un JWT utilizando el email como subject.

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .signWith(getSigningKey())
                .compact();
    }
}