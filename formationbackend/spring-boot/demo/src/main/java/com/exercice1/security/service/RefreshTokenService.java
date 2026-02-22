package com.exercice1.security.service;

import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.exercice1.security.exception.InvalidDataException;
import com.exercice1.security.model.RefreshToken;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    @Value("${jwt.refresh.expiration:604800000}") // 7 jours par défaut
    private long refreshTokenExpiration;
    
    @Value("${jwt.refresh.expiration.rememberMe:2592000000}") // 30 jours
    private long refreshTokenExpirationRememberMe;
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    /**
     * Créer un nouveau refresh token pour un utilisateur
     */
    @Transactional
    public RefreshToken createRefreshToken(User user, boolean rememberMe) {
        // 1. Supprimer les anciens tokens (1 seul token actif par user)
        refreshTokenRepository.deleteByUser(user);
        
        // 2. Calculer la durée selon rememberMe
        long expirationTime = rememberMe 
            ? refreshTokenExpirationRememberMe 
            : refreshTokenExpiration;
        
        // 3. Créer le token
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())  // ← UUID aléatoire
            .expiryDate(Instant.now().plusMillis(expirationTime))
            .revoked(false)
            .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    /**
     * Vérifier et récupérer un refresh token valide
     */
    public RefreshToken verifyRefreshToken(String token) {
        // 1. Chercher en DB
        RefreshToken refreshToken = refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new InvalidDataException("Invalid refresh token"));
        
        // 2. Vérifier si révoqué
        if (refreshToken.isRevoked()) {
            throw new InvalidDataException("Refresh token has been revoked");
        }
        
        // 3. Vérifier si expiré
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidDataException("Refresh token has expired");
        }
        
        return refreshToken;
    }
    
    /**
     * Révoquer un refresh token (logout)
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }


    @Transactional
    public void revokeUserRefreshToken(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }
}