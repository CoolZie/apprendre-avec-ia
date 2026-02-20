package com.exercice1.demo.dto;

import java.time.LocalDateTime;

import com.exercice1.demo.model.Product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Réponse contenant les informations complètes d'un produit")
public class ProductResponse {
    
    @Schema(description = "Identifiant unique du produit", example = "1")
    private Long id;
    
    @Schema(description = "Nom du produit", example = "Laptop HP Pavilion")
    private String name;
    
    @Schema(description = "Description détaillée du produit", example = "Ordinateur portable 15 pouces avec processeur Intel i7")
    private String description;
    
    @Schema(description = "Prix en euros", example = "899.99")
    private Double price;
    
    @Schema(description = "Quantité en stock", example = "50")
    private Integer stock;
    
    @Schema(description = "Catégorie du produit", example = "ELECTRONICS", allowableValues = {"ELECTRONICS", "BOOKS", "CLOTHING", "FOOD", "OTHER"})
    private String category;
    
    @Schema(description = "Date de création du produit", example = "2026-02-17T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date de dernière modification", example = "2026-02-17T14:45:00")
    private LocalDateTime updatedAt;

    public ProductResponse(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.category = product.getCategory().toString();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }

}
