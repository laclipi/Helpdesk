package com.laura.helpdesk.dto;

import com.laura.helpdesk.model.Priority;

import jakarta.validation.constraints.NotBlank;

// DTO con los datos necesarios para crear un ticket.
public record TicketCreateRequest(
        @NotBlank String title,
        @NotBlank String description,
        Priority priority

) {
}