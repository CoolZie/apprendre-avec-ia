package com.exercice1.security.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.exercice1.security.dto.RegisterRequest;
import com.exercice1.security.exception.InvalidCredentialsException;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    
    // Créer un utilisateur
    public User createUser(RegisterRequest request) {
        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))  // ✅ Chiffrer
            .build();
        
        return userRepository.save(user);
    }
    
    // Vérifier le mot de passe
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    // Changer le mot de passe
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect old password");
        }
        
        // Mettre à jour avec le nouveau (chiffré)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}