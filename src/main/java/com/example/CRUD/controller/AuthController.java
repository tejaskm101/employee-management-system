package com.example.CRUD.controller;

import com.example.CRUD.dto.RegisterRequestDTO;
import com.example.CRUD.dto.RegisterResponseDTO;
import com.example.CRUD.entity.User;
import com.example.CRUD.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.CRUD.dto.LoginRequestDTO;
import com.example.CRUD.dto.LoginResponseDTO;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        User user = authenticationService.register(request);

        RegisterResponseDTO response = new RegisterResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authenticationService.login(request);

        return ResponseEntity.ok(response);
    }
}
