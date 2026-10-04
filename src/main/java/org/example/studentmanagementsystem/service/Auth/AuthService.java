package org.example.studentmanagementsystem.service.Auth;

import org.example.studentmanagementsystem.dto.Auth.LoginRequestDto;
import org.example.studentmanagementsystem.dto.Auth.LoginResponseDto;
import org.example.studentmanagementsystem.dto.Auth.RegisterRequestDto;

public interface AuthService {

    void register(RegisterRequestDto request);

    LoginResponseDto login(LoginRequestDto request);
}