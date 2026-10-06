package com.laura.helpdesk.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laura.helpdesk.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Busca un usuario utilizando su email.
    Optional<User> findByEmail(String email);

}