package org.example.studentmanagementsystem.dto.Auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String token;

    private String tokenType;

    private Long expiresIn;
}