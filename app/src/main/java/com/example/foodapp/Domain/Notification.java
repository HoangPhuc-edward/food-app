package com.example.foodapp.Domain;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification {
    private String title;
    private String message;
    private String img;

    private String orderId;

    private String date;

    public Notification() {
    }

    public Notification(String title, String message, String img, String orderId, String date) {
        this.title = title;
        this.message = message;
        this.img = img;
        this.orderId = orderId;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @SuppressLint("SimpleDateFormat")
    public void create(String title, String message, String orderId){
        this.title = title;
        this.message = message;
        this.orderId = orderId;
        this.img = "no";
        this.date = new SimpleDateFormat("hh:mm a dd/MM/yyyy").format(new Date());
    }

    public static void addNotificationToFirebase(Order order, String title, String msg){
        Notification notification = new Notification();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notification.create(title, msg, order.getId());

        String imagePath = order.getFoodCarts().get(0).getFood().getImagePath();
        notification.setImg(imagePath);


        FirebaseDatabase.getInstance().getReference("Notification").child(uid).child(order.getId()).push().setValue(notification, notification.date);

    }
}
