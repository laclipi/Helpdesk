package com.laura.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laura.helpdesk.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}