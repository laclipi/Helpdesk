package com.laura.helpdesk.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.laura.helpdesk.model.User;
import com.laura.helpdesk.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Busca un usuario por su ID utilizando el repositorio.
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Busca un usuario utilizando su email.
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
