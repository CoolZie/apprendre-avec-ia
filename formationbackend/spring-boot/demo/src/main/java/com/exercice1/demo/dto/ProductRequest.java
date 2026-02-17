package com.exercice1.demo.dto;

import com.exercice1.demo.model.Category;
import com.exercice1.demo.model.Product;
import com.exercice1.demo.validation.ValidCategory;
import com.exercice1.demo.validation.ValidPrice;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ProductRequest {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    @Pattern(regexp = "^[a-zA-Z0-9\\s-]+$",message = "Le nom ne peut contenir que des lettres, chiffres, espaces et tirets")
    private String name;

    @Size(max = 1000, message = "La description ne peut dépasser 1000 caractères")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @ValidPrice
    private Double price;

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut être négatif")
    @Max(value = 10000,message ="Le stock ne peut dépasser 10000") 
    private Integer stock;

    @NotBlank(message = "La catégorie est obligatoire")
    @ValidCategory
    private String category;

    public  Product toEntity(){
        Product product = new Product();
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setStock(this.stock);
        product.setCategory(Category.valueOf(this.category) );
        return product;
    }
}
