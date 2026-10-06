package com.laura.helpdesk.exception;

// Se lanza cuando no encontramos el ticket solicitado.
public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String message) {
        super(message);
    }
}