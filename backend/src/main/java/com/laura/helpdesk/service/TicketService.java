package com.laura.helpdesk.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.laura.helpdesk.dto.TicketCreateRequest;
import com.laura.helpdesk.exception.AccessDeniedException;
import com.laura.helpdesk.exception.TicketNotFoundException;
import com.laura.helpdesk.model.Priority;
import com.laura.helpdesk.model.Ticket;
import com.laura.helpdesk.model.TicketStatus;
import com.laura.helpdesk.model.User;
import com.laura.helpdesk.repository.TicketRepository;
import com.laura.helpdesk.repository.UserRepository;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketService(
            TicketRepository ticketRepository,
            UserRepository userRepository) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    // Crea un nuevo ticket aplicando las reglas de negocio.
    public Ticket createTicket(TicketCreateRequest request, Long userId) {

        // Buscamos al usuario que está creando el ticket.
        User user = userRepository.findById(userId)
                .orElseThrow();

        // Creamos el ticket con los datos recibidos.
        Ticket ticket = new Ticket();

        ticket.setTitle(request.title());
        ticket.setDescription(request.description());

        // Asociamos el ticket con el usuario que lo está creando.
        ticket.setUser(user);

        // Todo ticket nuevo comienza en estado OPEN.
        ticket.setStatus(TicketStatus.OPEN);

        // Si no se indica prioridad, usamos MEDIUM por defecto.
        if (request.priority() == null) {
            ticket.setPriority(Priority.MEDIUM);
        } else {
            ticket.setPriority(request.priority());
        }

        // Guardamos la fecha y hora actuales.
        ticket.setCreatedAt(LocalDateTime.now());

        // Guardamos el ticket en la base de datos.
        return ticketRepository.save(ticket);
    }

    // Obtiene todos los tickets pertenecientes a un usuario.
    public List<Ticket> findMyTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    // Busca un ticket y comprueba que pertenece al usuario.
    public Ticket findById(Long ticketId, Long userId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElse(null);

        if (ticket == null) {
            throw new TicketNotFoundException(
                    "Ticket no encontrado: " + ticketId);
        }

        // El ticket existe, pero pertenece a otro usuario.
        if (!ticket.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(
                    "No tienes permiso para acceder a este ticket");
        }

        return ticket;
    }
}