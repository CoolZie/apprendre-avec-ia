package com.exercice1.demo.dto;

import java.time.LocalDateTime;

import com.exercice1.demo.model.Product;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private LocalDateTime createdAt;
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
