package com.example.foodapp.Domain;

public class Shipper extends User{
    private Double ratings;
    private String vehicle;
    private String status;

    public Shipper() {

    }

    public Shipper(String firstName, String lastName, String uid, String imagePath, String phoneNumber, String address, String method, Double ratings, String vehicle) {
        super(firstName, lastName, uid, imagePath, phoneNumber, address, method, 0);
        this.ratings = ratings;
        this.vehicle = vehicle;
        this.status = "not ready";
    }

    public Shipper(User user, String vehicle){
        super(user);
        this.ratings = 5.0;
        this.vehicle = vehicle;
        this.status = "ready";
    }

    public Double getRatings() {
        return ratings;
    }

    public void setRatings(Double ratings) {
        this.ratings = ratings;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
