package com.exercice1.demo.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
    @NotNull(message = "L'ID du client est obligatoire")
    Long customerId,
    
    @NotEmpty(message = "La commande doit contenir au moins un produit")
    List<OrderItemRequest> items
) {}