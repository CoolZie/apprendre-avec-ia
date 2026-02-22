package com.exercice1.security.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exercice1.security.dto.RoleUpdateRequest;
import com.exercice1.security.dto.UserResponse;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.UserRepository;
import com.exercice1.security.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Liste tous les utilisateurs (ADMIN seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    /**
     * Voir un utilisateur spécifique
     * - ADMIN : peut voir n'importe qui
     * - USER : peut voir seulement son propre profil
     */
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    /**
     * Changer les rôles d'un utilisateur (ADMIN seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/roles")
    public ResponseEntity<UserResponse> updateUserRoles(
        @PathVariable Long id,
        @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUserRoles(id, request));
    }
    
    /**
     * Supprimer un utilisateur (ADMIN seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    


    @GetMapping("/me")
public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    User user = userRepository.findByUsername(username).orElseThrow();

    return new UserResponse(user.getId(), username, user.getEmail(), user.getCreatedAt());
}
    
    

}
