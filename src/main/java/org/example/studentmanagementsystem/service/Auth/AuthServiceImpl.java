package org.example.studentmanagementsystem.service.Auth;

import lombok.RequiredArgsConstructor;
import org.example.studentmanagementsystem.Security.JwtService;
import org.example.studentmanagementsystem.dto.Auth.LoginRequestDto;
import org.example.studentmanagementsystem.dto.Auth.LoginResponseDto;
import org.example.studentmanagementsystem.dto.Auth.RegisterRequestDto;
import org.example.studentmanagementsystem.entity.Role;
import org.example.studentmanagementsystem.entity.User;
import org.example.studentmanagementsystem.exception.DuplicateResourceException;
import org.example.studentmanagementsystem.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void register(RegisterRequestDto request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already exists"
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(request.getPassword())
                )
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user);

        return LoginResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpiration())
                .build();
    }
}