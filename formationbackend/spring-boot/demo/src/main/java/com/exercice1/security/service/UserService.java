package com.exercice1.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.exercice1.demo.exception.InvalidDataException;
import com.exercice1.security.dto.RegisterRequest;
import com.exercice1.security.dto.RoleUpdateRequest;
import com.exercice1.security.dto.UserResponse;
import com.exercice1.security.exception.InvalidCredentialsException;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new InvalidDataException("User not found"));
        
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
    
    @Transactional
    public UserResponse updateUserRoles(Long id, RoleUpdateRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new InvalidDataException("User not found"));
        
        user.setRoles(request.getRoles());
        User updated = userRepository.save(user);
        
        return new UserResponse(
            updated.getId(),
            updated.getUsername(),
            updated.getEmail(),
            updated.getCreatedAt()
        );
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new InvalidDataException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    // Créer un utilisateur
    @Transactional
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
    @Transactional
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