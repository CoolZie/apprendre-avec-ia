package com.example.demo.model;

public enum LoanStatus {
    ACTIVE("Active"), RETURNED("Retourne"), OVERDUE("Passee");

    private final String displayName;

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
