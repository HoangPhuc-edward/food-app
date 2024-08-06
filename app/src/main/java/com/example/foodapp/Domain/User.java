package com.example.foodapp.Domain;

public class User {

    private String firstName;
    private String lastName;
    private String uid;
    private String imagePath;

    private String phoneNumber;
    private String address;
    private String method;

    private int orderNumber;

    private String token;

    public User() {
    }

    public User(String firstName, String lastName, String uid, String imagePath, String phoneNumber, String address, String method, int orderNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uid = uid;
        this.imagePath = imagePath;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.method = method;
        this.orderNumber = orderNumber;
    }

    public User(User user){
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.uid = user.getUid();
        this.imagePath = user.getImagePath();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.method = user.getMethod();
        this.orderNumber = user.orderNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
