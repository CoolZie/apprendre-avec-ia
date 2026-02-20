package com.exercice1.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Statistiques d'une catégorie de produits")
public class CategoryStatistics {
    
    @Schema(description = "Nom de la catégorie", example = "ELECTRONICS")
    private String category;
    
    @Schema(description = "Nombre de produits dans cette catégorie", example = "45")
    private long productCount;
    
    @Schema(description = "Prix moyen dans cette catégorie", example = "299.99")
    private double averagePrice;
    
    @Schema(description = "Valeur totale du stock de cette catégorie", example = "13499.55")
    private double totalValue;
}
