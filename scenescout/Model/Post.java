package com.example.mena.scenescout.Model;


import java.util.ArrayList;


public class Post {

    private ArrayList<String> imageUrls;

    private String title;
    private String street;
    private String city;
    private String province;
    private String postDesc;
    private int rating;
    private ArrayList<String> tagList;
    private String cost;
    private String costRate;
    private LocationSpec spec;
    private String latitude;
    private String longitude;
    private String email;
    private String phoneNum;

    public Post()
    {}

    public Post(ArrayList<String> imageUrls, String title, String street, String city, String province, String postDesc, int rating, ArrayList<String> tagList, String cost, String costRate, LocationSpec spec, String email, String phoneNum) {
        this.imageUrls = imageUrls;
        this.title = title;
        this.street = street;
        this.city = city;
        this.province = province;
        this.postDesc = postDesc;
        this.rating = rating;
        this.tagList = tagList;
        this.cost = cost;
        this.costRate = costRate;
        this.spec = spec;
        this.email = email;
        this.phoneNum = phoneNum;
        //this.longitude = longitude;
        //this.latitude = latitude;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String description) {
        this.city = description;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<String> tagList) {
        this.tagList = tagList;
    }

    public LocationSpec getSpec() {
        return spec;
    }

    public void setSpec(LocationSpec spec) {
        this.spec = spec;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCostRate() {
        return costRate;
    }

    public void setCostRate(String costRate) {
        this.costRate = costRate;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
