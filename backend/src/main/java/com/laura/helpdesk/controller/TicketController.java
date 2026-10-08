package com.laura.helpdesk.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laura.helpdesk.dto.TicketCreateRequest;
import com.laura.helpdesk.dto.TicketResponse;
import com.laura.helpdesk.dto.TicketUpdateRequest;
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
    // Crea un nuevo ticket.
    @PostMapping
    public TicketResponse createTicket(
            @Valid @RequestBody TicketCreateRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Long userId = user.getId();

        return ticketService.createTicket(request, userId);
    }

    // Obtiene todos los tickets pertenecientes al usuario actual.
    @GetMapping
    public List<TicketResponse> findMyTickets(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Long userId = user.getId();

        return ticketService.findMyTickets(userId);
    }

    // Obtiene un ticket concreto del usuario actual.
    @GetMapping("/{id}")
    public TicketResponse findById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Long userId = user.getId();

        return ticketService.findById(id, userId);
    }

    // Actualiza un ticket utilizando el usuario autenticado obtenido del JWT.
    @PutMapping("/{id}")
    public TicketResponse updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateRequest request,
            Authentication authentication) {

        // Obtenemos el email del usuario autenticado desde el JWT.
        String email = authentication.getName();

        // Buscamos en la base de datos al usuario asociado a ese email.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado"));

        // Obtenemos el ID real del usuario para comprobar el ownership.
        Long userId = user.getId();

        // Delegamos la actualización al servicio.
        return ticketService.updateTicket(id, userId, request);
    }

    // Permite eliminar un ticket del usuario autenticado.
    @DeleteMapping("/{id}")
    public void deleteTicket(
            @PathVariable Long id,
            Authentication authentication) {

        // Obtenemos el email del usuario autenticado desde el JWT.
        String email = authentication.getName();

        // Buscamos en la base de datos al usuario asociado a ese email.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado"));

        // Obtenemos el ID real del usuario para comprobar el ownership.
        Long userId = user.getId();

        // Delegamos la eliminación al servicio.
        ticketService.deleteTicket(id, userId);
    }

}