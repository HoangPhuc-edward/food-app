package com.example.foodapp.Domain;

import android.annotation.SuppressLint;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Order {
    private ArrayList<FoodCart> foodCarts;
    private User user;
    private Shipper shipper;
    private String id;

    private int status;

    private String createTime;
    private String readyTime;
    private String shippingTime;
    private String finishTime;
    private String cancelTime;

    private Double total;
    private Double subTotal;
    private Double deliveryTax;
    private Double totalTax;

    public Order() {
    }

    public Order(ArrayList<FoodCart> foodCarts, User user, Shipper shipper, String id, int status, String createTime, String readyTime, String shippingTime, String finishTime, String cancelTime, Double total, Double subTotal, Double deliveryTax, Double totalTax) {
        this.foodCarts = foodCarts;
        this.user = user;
        this.shipper = shipper;
        this.id = id;
        this.status = status;
        this.createTime = createTime;
        this.readyTime = readyTime;
        this.shippingTime = shippingTime;
        this.finishTime = finishTime;
        this.cancelTime = cancelTime;
        this.total = total;
        this.subTotal = subTotal;
        this.deliveryTax = deliveryTax;
        this.totalTax = totalTax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<FoodCart> getFoodCarts() {
        return foodCarts;
    }

    public void setFoodCarts(ArrayList<FoodCart> foodCarts) {
        this.foodCarts = foodCarts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(String readyTime) {
        this.readyTime = readyTime;
    }

    public String getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(String shippingTime) {
        this.shippingTime = shippingTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getDeliveryTax() {
        return deliveryTax;
    }

    public void setDeliveryTax(Double deliveryTax) {
        this.deliveryTax = deliveryTax;
    }

    public Double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(Double totalTax) {
        this.totalTax = totalTax;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void create(ArrayList<FoodCart> foodCarts, User user, Double subTotal, Double deliveryTax, Double totalTax, Double total){
        this.id = String.valueOf(user.getOrderNumber());
        this.foodCarts = foodCarts;
        this.user = user;
        this.shipper = new Shipper(user, "hello");


        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd/MM/yyyy");
        this.createTime = simpleDateFormat.format(new Date());
        this.status = 0;
        this.readyTime = "";
        this.shippingTime = "";
        this.finishTime = "";
        this.cancelTime = "";
        this.total = total;
        this.subTotal = subTotal;
        this.deliveryTax = deliveryTax;
        this.totalTax = totalTax;


        user.setOrderNumber(user.getOrderNumber() + 1);
        FirebaseDatabase.getInstance().getReference("User").child(user.getUid()).setValue(user);
    }
}
