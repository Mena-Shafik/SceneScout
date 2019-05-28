package com.example.mena.scenescout.Model;


public class LocationSpec {

    private Boolean parking;
    private Boolean smoking;
    private Boolean electricity;
    private Boolean wifi;
    private Boolean restroom;
    private Boolean pets;
    private Boolean garage;
    private Boolean kitchen;
    private Boolean wheelchair;
    private Boolean eatingArea;
    private Boolean foodResit;

    public LocationSpec()
    {}

    public LocationSpec(Boolean parking, Boolean smoking, Boolean electricity, Boolean wifi, Boolean restroom, Boolean pets, Boolean garage, Boolean kitchen, Boolean wheelchair, Boolean eatingArea, Boolean foodResit) {
        this.parking = parking;
        this.smoking = smoking;
        this.electricity = electricity;
        this.wifi = wifi;
        this.restroom = restroom;
        this.pets = pets;
        this.garage = garage;
        this.kitchen = kitchen;
        this.wheelchair = wheelchair;
        this.eatingArea = eatingArea;
        this.foodResit = foodResit;
    }

    public Boolean getParking() {
        return parking;
    }

    public void setParking(Boolean parking) {
        this.parking = parking;
    }

    public Boolean getSmoking() {
        return smoking;
    }

    public void setSmoking(Boolean smoking) {
        this.smoking = smoking;
    }

    public Boolean getElectricity() {
        return electricity;
    }

    public void setElectricity(Boolean electricity) {
        this.electricity = electricity;
    }

    public Boolean getWifi() {
        return wifi;
    }

    public void setWifi(Boolean wifi) {
        this.wifi = wifi;
    }

    public Boolean getRestroom() {
        return restroom;
    }

    public void setRestroom(Boolean restroom) {
        this.restroom = restroom;
    }

    public Boolean getPets() {
        return pets;
    }

    public void setPets(Boolean pets) {
        this.pets = pets;
    }

    public Boolean getGarage() {
        return garage;
    }

    public void setGarage(Boolean garage) {
        this.garage = garage;
    }

    public Boolean getKitchen() {
        return kitchen;
    }

    public void setKitchen(Boolean kitchen) {
        this.kitchen = kitchen;
    }

    public Boolean getWheelchair() {
        return wheelchair;
    }

    public void setWheelchair(Boolean wheelchair) {
        this.wheelchair = wheelchair;
    }

    public Boolean getEatingArea() {
        return eatingArea;
    }

    public void setEatingArea(Boolean eatingArea) {
        this.eatingArea = eatingArea;
    }

    public Boolean getFoodResit() {
        return foodResit;
    }

    public void setFoodResit(Boolean foodResit) {
        this.foodResit = foodResit;
    }
}
