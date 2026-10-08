// Este servicio contiene la lógica de negocio de los tickets:
// creación, consulta, actualización y eliminación, incluyendo las comprobaciones de ownership.
package com.laura.helpdesk.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.laura.helpdesk.dto.TicketCreateRequest;
import com.laura.helpdesk.dto.TicketResponse;
import com.laura.helpdesk.dto.TicketUpdateRequest;
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
    public TicketResponse createTicket(TicketCreateRequest request, Long userId) {

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
        Ticket savedTicket = ticketRepository.save(ticket);

        // Convertimos la entidad JPA en el DTO que devolverá la API.
        return toResponse(savedTicket);
    }

    // Obtiene todos los tickets pertenecientes a un usuario.
    public List<TicketResponse> findMyTickets(Long userId) {

        // Buscamos los tickets del usuario y convertimos cada entidad en un DTO.
        return ticketRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Busca un ticket y comprueba que pertenece al usuario.
    public TicketResponse findById(Long ticketId, Long userId) {

        // Buscamos el ticket por su identificador.
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElse(null);

        // Si no existe, lanzamos una excepción de ticket no encontrado.
        if (ticket == null) {
            throw new TicketNotFoundException(
                    "Ticket no encontrado: " + ticketId);
        }

        // El ticket existe, pero pertenece a otro usuario.
        if (!ticket.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(
                    "No tienes permiso para acceder a este ticket");
        }

        // Devolvemos el ticket convertido al DTO.
        return toResponse(ticket);
    }

    // Actualiza un ticket comprobando primero que pertenece al usuario autenticado.
    public TicketResponse updateTicket(
            Long ticketId,
            Long userId,
            TicketUpdateRequest request) {

        // Buscamos el ticket que queremos modificar.
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(
                        "Ticket no encontrado: " + ticketId));

        // Comprobamos que el ticket pertenece al usuario que realiza la petición.
        if (!ticket.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(
                    "No tienes permiso para modificar este ticket");
        }

        // Actualizamos únicamente los campos que permitimos modificar.
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());

        // Guardamos los cambios en la base de datos.
        Ticket updatedTicket = ticketRepository.save(ticket);

        // Convertimos la entidad JPA en el DTO que devolverá la API.
        return toResponse(updatedTicket);
    }

    // Elimina un ticket comprobando primero que pertenece al usuario autenticado.
    public void deleteTicket(Long ticketId, Long userId) {

        // Buscamos el ticket que queremos eliminar.
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(
                        "Ticket no encontrado: " + ticketId));

        // Comprobamos que el ticket pertenece al usuario que realiza la petición.
        if (!ticket.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(
                    "No tienes permiso para eliminar este ticket");
        }

        // Eliminamos el ticket de la base de datos.
        ticketRepository.delete(ticket);
    }

    // Convierte una entidad Ticket en un TicketResponse para controlar los datos
    // que expone la API.
    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                ticket.getUser().getId(),
                ticket.getUser().getEmail());
    }
}