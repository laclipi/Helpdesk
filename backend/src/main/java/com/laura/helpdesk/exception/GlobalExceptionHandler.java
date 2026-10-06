package com.laura.helpdesk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Gestiona de forma global las excepciones de nuestros Controllers.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Convierte TicketNotFoundException en una respuesta HTTP 404.
    @ExceptionHandler(TicketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTicketNotFound(TicketNotFoundException exception) {

        return exception.getMessage();
    }

    // Convierte AccessDeniedException en una respuesta HTTP 403.
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException exception) {

        return exception.getMessage();
    }
}