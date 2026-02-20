package com.exercice1.security.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exercice1.security.dto.AuthResponse;
import com.exercice1.security.dto.LoginRequest;
import com.exercice1.security.dto.RegisterRequest;
import com.exercice1.security.dto.UserResponse;
import com.exercice1.security.exception.DuplicateResourceException;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.UserRepository;
import com.exercice1.security.security.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        // Créer le nouvel utilisateur
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of("ROLE_USER"))  // Rôle par défaut
            .build();
        
        userRepository.save(user);
        
        // Générer le token JWT
        String token = jwtService.generateToken(toUserDetails(user));
        
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(),user.getEmail()));
    }

    @GetMapping("/me")
public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    User user = userRepository.findByUsername(username).orElseThrow();

    return new UserResponse(user.getId(), username, user.getEmail(), user.getCreatedAt());
}
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Authentifier l'utilisateur
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow();
        
        // Générer le token JWT
        String token = jwtService.generateToken(toUserDetails(user));
        
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(),user.getEmail()));
    }
    
    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()))
            .build();
    }
}