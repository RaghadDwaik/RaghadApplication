package com.example.myapplication;

public class ServicesClass {

    public String getId() {
        return id;
    }

    private String id;
    private String name;

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    private String placename;
    private String image;
    private double price;
    private String description;

    public ServicesClass() {}

    public ServicesClass(String id,String name, String image, double price, String description) {
        this.id=id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
    }

    public ServicesClass(String name, String image, double price, String description, String placename) {

        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
        this.placename=placename;
    }

    public ServicesClass(String id,String name, String image, double price) {
        this.id=id;
        this.name = name;
        this.image = image;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
