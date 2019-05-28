package com.example.mena.scenescout.Model;

public class User {

    private String imageUrl;
    private String name = "John Doe";
    private String email = "fail@mena.com";
    private String phoneNum = "000-000-0000";
    private String about = "idk";



    public User(String name, String email, String about, String phoneNum) {
        this.name = name;
        this.email = email;
        this.about = about;
        this.phoneNum = phoneNum;
    }


    public User(String imageUrl, String name, String email, String about, String phoneNum) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.email = email;
        this.about = about;
        this.phoneNum = phoneNum;
    }


    public User()
    {

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
