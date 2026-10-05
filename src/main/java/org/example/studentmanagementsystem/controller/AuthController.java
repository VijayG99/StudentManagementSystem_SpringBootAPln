package org.example.studentmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.studentmanagementsystem.dto.Auth.LoginRequestDto;
import org.example.studentmanagementsystem.dto.Auth.LoginResponseDto;
import org.example.studentmanagementsystem.dto.Auth.RegisterRequestDto;
import org.example.studentmanagementsystem.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequestDto request) {

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        LoginResponseDto response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}