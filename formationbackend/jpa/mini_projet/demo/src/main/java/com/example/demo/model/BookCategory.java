package com.example.demo.model;

public enum BookCategory {
    FICTION("Fiction"),
    NON_FICTION("Non Fiction"),
    SCIENCE("Science"),
    HISTORY("Histoire");
    private final String displayName;
    BookCategory(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
