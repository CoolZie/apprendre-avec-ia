package com.exercice1.demo.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Statistiques globales sur l'ensemble du catalogue de produits")
public class ProductStatistics {
    
    @Schema(description = "Nombre total de produits", example = "150")
    private long totalProducts;
    
    @Schema(description = "Valeur totale du stock (prix x quantité)", example = "125000.50")
    private double totalValue;
    
    @Schema(description = "Prix moyen des produits", example = "45.99")
    private double averagePrice;
    
    @Schema(description = "Nombre de produits par catégorie")
    private Map<String, Long> categoryCounts;
    
    @Schema(description = "Nombre de produits à stock faible (< 10)", example = "12")
    private long lowStockCount;
    
    @Schema(description = "Nombre de produits en rupture de stock", example = "3")
    private long outOfStockCount;
}
