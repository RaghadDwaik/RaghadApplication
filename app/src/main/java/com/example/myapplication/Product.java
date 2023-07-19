package com.example.myapplication;

public class Product {
    private String name;
    private String description;

    // Default constructor (required for Firebase)
    public Product() {
    }

    public Product(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
