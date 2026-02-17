package com.exercice1.demo.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductStatistics {
    private long totalProducts;
    private double totalValue;
    private double averagePrice;
    private Map<String, Long> categoryCounts;
    private long lowStockCount;
    private long outOfStockCount;
}
