package com.example.demo.exception;



public class LoanNotExistExceededException extends RuntimeException {
    public LoanNotExistExceededException(String message) {
        super(message);
    }
}
