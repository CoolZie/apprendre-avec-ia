package com.exercice1.demo.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message){
        super(message);
    }
}
