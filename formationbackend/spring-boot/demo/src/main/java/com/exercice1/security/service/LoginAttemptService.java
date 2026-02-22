package com.exercice1.security.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_HOURS = 1;
    
    // Map : username -> nombre de tentatives
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    
    // Map : username -> date de fin de blocage
    private final Map<String, LocalDateTime> blockCache = new ConcurrentHashMap<>();
    
    /**
     * Appelé après un login réussi
     */
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockCache.remove(username);
    }
    
    /**
     * Appelé après un échec de login
     */
    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            blockCache.put(username, LocalDateTime.now().plusHours(BLOCK_DURATION_HOURS));
        }
    }
    
    /**
     * Vérifie si un utilisateur est bloqué
     */
    public boolean isBlocked(String username) {
        if (!blockCache.containsKey(username)) {
            return false;
        }
        
        LocalDateTime blockUntil = blockCache.get(username);
        if (LocalDateTime.now().isAfter(blockUntil)) {
            // Le blocage est expiré
            blockCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }
        
        return true;
    }
    
    /**
     * Nombre de tentatives restantes avant blocage
     */
    public int getRemainingAttempts(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        return Math.max(0, MAX_ATTEMPTS - attempts);
    }
    
    /**
     * Temps restant de blocage (en minutes)
     */
    public long getRemainingBlockTime(String username) {
        if (!blockCache.containsKey(username)) {
            return 0;
        }
        
        LocalDateTime blockUntil = blockCache.get(username);
        long minutes = java.time.Duration.between(LocalDateTime.now(), blockUntil).toMinutes();
        return Math.max(0, minutes);
    }
}