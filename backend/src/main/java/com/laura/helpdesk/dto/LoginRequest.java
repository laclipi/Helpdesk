package com.laura.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO con los datos necesarios para iniciar sesión.
public record LoginRequest(

        // El email es obligatorio y debe tener formato válido.
        @NotBlank @Email String email,

        // La contraseña es obligatoria.
        @NotBlank String password) {
}