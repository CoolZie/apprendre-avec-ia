package com.exercice1.demo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CategoryValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategory {

    String message() default "Catégorie invalide. Valeurs acceptées : ELECTRONICS, CLOTHING, FOOD, BOOKS, OTHER";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}