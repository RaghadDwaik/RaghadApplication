package com.example.myapplication;

import com.google.firebase.database.PropertyName;

import java.util.HashMap;

public class ServicesClass {

    public String getId() {
        return id;
    }

    private String id;
    private String name;


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResturant() {
        return Resturant;
    }

    public void setResturant(String Resturant) {
        this.Resturant = Resturant;
    }

    public String getDryClean() {
        return DryClean;
    }

    public void setDryClean(String DryClean) {
        DryClean = DryClean;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }

    public String getSupermarket() {
        return Supermarket;
    }

    public void setSupermarket(String Supermarket) {
        this.Supermarket = Supermarket;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String Resturant;

    @PropertyName("DryClean")
    private String DryClean;

    @PropertyName("salon")
    private String salon;

    @PropertyName("Supermarket")
    private String Supermarket;

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

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("image", image);
        map.put("price", price);
        map.put("description", description);
        map.put("Resturant", Resturant);
        map.put("DryClean", DryClean);
        map.put("salon", salon);
        map.put("Supermarket", Supermarket);
        return map;
    }

    public ServicesClass(String name, String image, double price, String description,String DryClean,String Supermarket ,String salon, String Resturant) {

        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
        this.Resturant=Resturant;

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
