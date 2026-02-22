package com.exercice1.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;       // ← Renommé (avant "token")
    private String refreshToken;      // ← Nouveau
    private String username;
    private String email;
    private String tokenType;         // ← "Bearer"
    private long expiresIn;           // ← Durée en millisecondes
}