package com.exercice1.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceValidator implements ConstraintValidator<ValidPrice, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        // BUG #5 CORRIGÉ: 100000 au lieu de 1000000
        return value >= 0.01 && value <= 100000;
    }

}
