package com.laura.helpdesk.dto;

// DTO que contiene la respuesta que devolvemos después de un login correcto.
public record LoginResponse(

        // Token JWT que utilizará el cliente en las siguientes peticiones.
        String token) {
}