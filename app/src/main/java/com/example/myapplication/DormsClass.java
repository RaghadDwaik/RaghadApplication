package com.example.myapplication;

public class DormsClass {
    private String image1;

    private String dorms;
    private String studyplace;

    public String getDorms() {
        return dorms;
    }

    public void setDorms(String dorms) {
        this.dorms = dorms;
    }

    public String getStudyplace() {
        return studyplace;
    }

    public void setStudyplace(String studyplace) {
        this.studyplace = studyplace;
    }

    public DormsClass(String image1, String id, String image2, String image3, String image4, String description, float rating) {
        this.image1 = image1;
        this.id = id;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.description = description;
        this.rating = rating;
    }

    public DormsClass(String image1, String image2, String image3,  String description, String dorms,String studyplace) {
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.description = description;
        this.dorms=dorms;
        this.studyplace=studyplace;
    }
    public DormsClass(String id,String name, String image){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getImage1() {
        return image1;
    }
    public DormsClass(){

    }
    public DormsClass(String image1, String image2, String image3, String description, float rating) {
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.description = description;
        this.rating = rating;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    private String image2;
    private String image3;

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    private String image4;

    private String description;
    private float rating;


}
