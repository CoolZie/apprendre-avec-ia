package com.exercice1.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
    @NotNull(message = "L'ID du produit est obligatoire")
    Long productId,
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Max(value = 1000, message = "La quantité ne peut dépasser 1000")
    Integer quantity
) {}