package com.laura.helpdesk.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.laura.helpdesk.dto.LoginRequest;
import com.laura.helpdesk.dto.LoginResponse;
import com.laura.helpdesk.dto.RegisterRequest;
import com.laura.helpdesk.model.Role;
import com.laura.helpdesk.model.User;
import com.laura.helpdesk.repository.UserRepository;
import com.laura.helpdesk.security.JwtService;

// Service encargado de la lógica relacionada con autenticación y usuarios.
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Spring inyecta automáticamente el UserRepository y el PasswordEncoder.
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    // Inicia sesión comprobando las credenciales del usuario.
    public LoginResponse login(LoginRequest request) {

        // Buscamos el usuario utilizando su email.
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas"));

        // Comprobamos que la contraseña recibida coincide
        // con el hash almacenado en la base de datos.
        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash())) {

            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        // Generamos un JWT para el usuario autenticado.
        String token = jwtService.generateToken(user.getEmail());

        // Devolvemos el token dentro de nuestro DTO de respuesta.
        return new LoginResponse(token);
    }
}