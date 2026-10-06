package com.laura.helpdesk.exception;

// Se lanza cuando un usuario intenta acceder a un recurso que no le pertenece.
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}