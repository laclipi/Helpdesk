package com.laura.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laura.helpdesk.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}