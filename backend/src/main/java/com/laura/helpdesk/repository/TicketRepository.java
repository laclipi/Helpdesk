package com.laura.helpdesk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laura.helpdesk.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Busca todos los tickets pertenecientes a un usuario.
    List<Ticket> findByUserId(Long userId);

}