package com.exercice1.security.dto;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    
    @NotEmpty(message = "Roles cannot be empty")
    private Set<@Pattern(
        regexp = "^ROLE_(USER|MODERATOR|ADMIN)$", 
        message = "Invalid role. Must be ROLE_USER, ROLE_MODERATOR, or ROLE_ADMIN"
    ) String> roles;
}