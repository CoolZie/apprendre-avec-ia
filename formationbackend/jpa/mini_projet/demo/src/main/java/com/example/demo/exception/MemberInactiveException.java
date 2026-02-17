package com.example.demo.exception;


public class MemberInactiveException extends RuntimeException {
    public MemberInactiveException(String message) {
        super(message);
    }
}