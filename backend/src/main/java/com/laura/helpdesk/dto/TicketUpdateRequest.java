package com.laura.helpdesk.dto;

import com.laura.helpdesk.model.Priority;

public record TicketUpdateRequest(
        String title,
        String description,
        Priority priority) {
}