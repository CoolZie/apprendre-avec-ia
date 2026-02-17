package com.exercice1.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryStatistics {
    private String category;
    private long productCount;
    private double averagePrice;
    private double totalValue;
}
