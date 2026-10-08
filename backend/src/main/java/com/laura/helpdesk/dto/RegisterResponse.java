// Este DTO define exactamente qué datos de usuario puede devolver la API
// después de un registro, evitando exponer el passwordHash.
package com.laura.helpdesk.dto;

public record RegisterResponse(
        Long id,
        String email,
        String role) {
}