package com.exercice1.security.controller;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exercice1.demo.exception.InvalidDataException;
import com.exercice1.security.dto.AuthResponse;
import com.exercice1.security.dto.ChangePasswordRequest;
import com.exercice1.security.dto.LoginRequest;
import com.exercice1.security.dto.RefreshTokenRequest;
import com.exercice1.security.dto.RegisterRequest;
import com.exercice1.security.dto.UserResponse;
import com.exercice1.security.dto.VerificationRequest;
import com.exercice1.security.exception.AccountBlockedException;
import com.exercice1.security.exception.DuplicateResourceException;
import com.exercice1.security.model.RefreshToken;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.UserRepository;
import com.exercice1.security.security.EmailService;
import com.exercice1.security.security.JwtService;
import com.exercice1.security.service.LoginAttemptService;
import com.exercice1.security.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Générer token de vérification
        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of("ROLE_USER"))
                .enabled(false) // ← Pas activé par défaut
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();

        userRepository.save(user);

        // Envoyer email de vérification
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        // Générer tokens (même si non vérifié, pour tester)
        String accessToken = jwtService.generateToken(toUserDetails(user), false);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, false);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .username(user.getUsername())
                .email(user.getEmail())
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime(false))
                .build());
    }

    /**
 * Renvoyer un email de vérification
 */
@PostMapping("/resend-verification")
public ResponseEntity<String> resendVerification(@Valid @RequestBody VerificationRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new InvalidDataException("User not found"));
    
    if (user.isEnabled()) {
        throw new InvalidDataException("Email already verified");
    }
    
    // Générer nouveau token
    String newToken = UUID.randomUUID().toString();
    user.setVerificationToken(newToken);
    user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
    userRepository.save(user);
    
    // Renvoyer email
    emailService.sendVerificationEmail(user.getEmail(), newToken);
    
    return ResponseEntity.ok("Verification email sent. Please check your inbox.");
}
/**
 * Changer le mot de passe de l'utilisateur connecté
 */
@PostMapping("/change-password")
public ResponseEntity<String> changePassword(
    @AuthenticationPrincipal UserDetails userDetails,
    @Valid @RequestBody ChangePasswordRequest request) {
    
    String username = userDetails.getUsername();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new InvalidDataException("User not found"));
    
    // Vérifier l'ancien mot de passe
    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
        throw new InvalidDataException("Old password is incorrect");
    }
    
    // Vérifier que le nouveau est différent
    if (request.getOldPassword().equals(request.getNewPassword())) {
        throw new InvalidDataException("New password must be different from old password");
    }
    
    // Changer le mot de passe
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
    
    // Invalider tous les refresh tokens de l'utilisateur
    refreshTokenService.revokeUserRefreshToken(user);
    
    // Envoyer email de notification
    emailService.sendPasswordChangedEmail(user.getEmail(), user.getUsername());
    
    return ResponseEntity.ok("Password changed successfully. Please login again.");
}
    /**
     * Vérifier l'email avec le token reçu par email
     */
    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verifyEmail(@PathVariable String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidDataException("Invalid verification token"));

        // Vérifier si le token n'est pas expiré
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Verification token has expired");
        }

        // Activer l'utilisateur
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Email verified successfully. You can now login.");
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        return new UserResponse(user.getId(), username, user.getEmail(), user.getCreatedAt());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // 1. Vérifier le refresh token
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();

        // 2. Générer un nouveau access token
        String accessToken = jwtService.generateToken(toUserDetails(user), false);

        // 3. Retourner les tokens (même refresh token)
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken()) // ← Même refresh token
                .username(user.getUsername())
                .email(user.getEmail())
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime(false))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Vérifier si le compte est bloqué
        if (loginAttemptService.isBlocked(request.getUsername())) {
            long remainingMinutes = loginAttemptService.getRemainingBlockTime(request.getUsername());
            throw new AccountBlockedException(
                    "Account temporarily blocked due to too many failed login attempts. " +
                            "Try again in " + remainingMinutes + " minutes.");
        }

        try {
            // 2. Authentifier
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow();

            // 3. Vérifier si l'email est vérifié
            if (!user.isEnabled()) {
                throw new InvalidDataException(
                        "Email not verified. Please check your inbox and verify your email address.");
            }

            // 4. Login réussi → reset tentatives
            loginAttemptService.loginSucceeded(request.getUsername());

            // 5. Générer tokens
            boolean rememberMe = request.isRememberMe();
            String accessToken = jwtService.generateToken(toUserDetails(user), rememberMe);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, rememberMe);

            return ResponseEntity.ok(AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationTime(rememberMe))
                    .build());

        } catch (BadCredentialsException ex) {
            // 6. Login échoué → incrémenter tentatives
            loginAttemptService.loginFailed(request.getUsername());
            int remaining = loginAttemptService.getRemainingAttempts(request.getUsername());

            throw new InvalidDataException(
                    "Invalid username or password. Remaining attempts: " + remaining);
        }
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
