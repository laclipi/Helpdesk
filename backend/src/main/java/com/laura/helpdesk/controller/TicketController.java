package com.laura.helpdesk.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laura.helpdesk.dto.TicketCreateRequest;
import com.laura.helpdesk.model.Ticket;
import com.laura.helpdesk.model.User;
import com.laura.helpdesk.repository.UserRepository;
import com.laura.helpdesk.service.TicketService;

import jakarta.validation.Valid;

// Controlador encargado de recibir las peticiones HTTP relacionadas con tickets.
@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserRepository userRepository;
    // spring inyecta autómaticamente el ticketservice y el userrepository

    public TicketController(
            TicketService ticketService,
            UserRepository userRepository) {

        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    // Crea un nuevo ticket.
    @PostMapping
    public Ticket createTicket(
            @Valid @RequestBody TicketCreateRequest request) {

        // De momento usamos un ID temporal del usuario.
        // Más adelante lo obtendremos del usuario autenticado mediante JWT.
        Long userId = 1L;

        return ticketService.createTicket(request, userId);
    }

    // Obtiene todos los tickets pertenecientes al usuario actual.
    @GetMapping
    public List<Ticket> findMyTickets(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Long userId = user.getId();

        return ticketService.findMyTickets(userId);
    }

    // Obtiene un ticket concreto del usuario actual.
    @GetMapping("/{id}")
    public Ticket findById(@PathVariable Long id) {

        // De momento usamos un ID temporal.
        // Más adelante lo obtendremos del JWT.
        Long userId = 1L;

        return ticketService.findById(id, userId);
    }

}