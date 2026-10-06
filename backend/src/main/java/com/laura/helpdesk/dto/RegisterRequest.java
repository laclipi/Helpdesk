package com.laura.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO que contiene los datos necesarios para registrar un usuario.
public record RegisterRequest(

        // El email es obligatorio y debe tener formato de email.
        @NotBlank @Email String email,

        // La contraseña es obligatoria.
        @NotBlank String password) {
}