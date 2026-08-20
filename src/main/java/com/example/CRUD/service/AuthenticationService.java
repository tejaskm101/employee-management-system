package com.example.CRUD.service;

import com.example.CRUD.dto.RegisterRequestDTO;
import com.example.CRUD.entity.User;
import com.example.CRUD.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.CRUD.exception.UsernameAlreadyExistsException;
import com.example.CRUD.dto.LoginRequestDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.example.CRUD.security.JwtService;
import com.example.CRUD.dto.LoginResponseDTO;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    public LoginResponseDTO login(LoginRequestDTO request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var userDetails =
                (org.springframework.security.core.userdetails.UserDetails)
                        authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        return new LoginResponseDTO(
                token,
                user.getUsername(),
                user.getRole()
        );
    }
    public User register(RegisterRequestDTO request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(
                    "Username already exists: " + request.getUsername()
            );
        }

        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole("ROLE_USER");

        return userRepository.save(user);
    }
}
