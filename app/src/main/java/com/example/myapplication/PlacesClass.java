package com.example.myapplication;


import java.util.Comparator;

public class PlacesClass implements Comparator<PlacesClass> {
    private String name;

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    private String ownerid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String image;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    private float rating;


    public PlacesClass(){

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public PlacesClass(String name, String image) {
        this.name = name;
        this.image = image;
    }
    public PlacesClass(String name) {
        this.name = name;

    }

    public PlacesClass(String name, String image, String ownerid) {
        this.ownerid=ownerid;
        this.name = name;
        this.image = image;
    }


    @Override
    public int compare(PlacesClass t,PlacesClass t1) {
        return Double.compare(t1.getRating(), t.getRating());
    }
}
