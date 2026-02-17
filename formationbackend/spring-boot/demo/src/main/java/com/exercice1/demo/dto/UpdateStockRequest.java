package com.exercice1.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStockRequest {
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = -10000,message = "La quantité minimale est -10000")
    @Max(value = 10000,message="La quantité maximale est 10000")
    private Integer quantity;

}
