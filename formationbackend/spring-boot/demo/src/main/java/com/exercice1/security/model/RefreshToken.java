package com.exercice1.security.model;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                  // ← L'utilisateur propriétaire
    
    private String token;                // ← UUID aléatoire
    
    private Instant expiryDate;          // ← Date d'expiration
    
    private boolean revoked;             // ← Si révoqué (logout)
}