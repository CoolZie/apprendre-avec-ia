package com.exercice1.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.exercice1.security.model.RefreshToken;
import com.exercice1.security.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    // Trouver un token par sa valeur
    Optional<RefreshToken> findByToken(String token);
    
    // Supprimer tous les tokens d'un utilisateur (logout all devices)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(User user);
    
    // Nettoyer les tokens expirés (tâche périodique)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

    Optional<RefreshToken> findByUser(User user);
}