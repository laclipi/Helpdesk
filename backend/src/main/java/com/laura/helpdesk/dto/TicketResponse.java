package com.laura.helpdesk.dto;

import java.time.LocalDateTime;

import com.laura.helpdesk.model.Priority;
import com.laura.helpdesk.model.TicketStatus;

public record TicketResponse(
        Long id,
        String title,
        String description,
        Priority priority,
        TicketStatus status,
        LocalDateTime createdAt,
        Long userId,
        String userEmail) {
}