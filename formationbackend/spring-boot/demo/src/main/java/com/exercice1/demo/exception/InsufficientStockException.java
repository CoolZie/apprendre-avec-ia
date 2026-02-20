package com.exercice1.demo.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format("Stock insuffisant pour %s : %d demandé(s), %d disponible(s)", 
            productName, requested, available));
    }
}