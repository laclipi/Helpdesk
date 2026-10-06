package com.laura.helpdesk.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.laura.helpdesk.dto.RegisterRequest;
import com.laura.helpdesk.model.Role;
import com.laura.helpdesk.model.User;
import com.laura.helpdesk.repository.UserRepository;

// Service encargado de la lógica relacionada con autenticación y usuarios.
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring inyecta automáticamente el UserRepository y el PasswordEncoder.
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registra un nuevo usuario.
    public User register(RegisterRequest request) {

        // Comprobamos si ya existe un usuario con ese email.
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Creamos el nuevo usuario.
        User user = new User();

        // Guardamos el email recibido.
        user.setEmail(request.email());

        // Nunca guardamos la contraseña original.
        // BCrypt genera un hash que será almacenado en la BD.
        user.setPasswordHash(
                passwordEncoder.encode(request.password()));

        // Todo usuario que se registra comienza con ROLE USER.
        user.setRole(Role.USER);

        // Guardamos el usuario en la base de datos.
        return userRepository.save(user);
    }
}