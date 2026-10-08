package com.laura.helpdesk.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laura.helpdesk.dto.LoginRequest;
import com.laura.helpdesk.dto.LoginResponse;
import com.laura.helpdesk.dto.RegisterRequest;
import com.laura.helpdesk.dto.RegisterResponse;
import com.laura.helpdesk.model.User;
import com.laura.helpdesk.service.AuthService;

import jakarta.validation.Valid;

// Controller encargado de las operaciones de autenticación.
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // Spring inyecta automáticamente el AuthService.
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Registra un nuevo usuario.
    // Registra un nuevo usuario y devuelve únicamente datos públicos.
    @PostMapping("/register")
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request) {

        // Delegamos la creación del usuario en el AuthService.
        User user = authService.register(request);

        // Devolvemos únicamente los datos que queremos exponer.
        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name());
    }

    // Inicia sesión y devuelve un JWT.
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        // Delegamos la comprobación de credenciales y generación del JWT
        // en el AuthService.
        return authService.login(request);
    }
}