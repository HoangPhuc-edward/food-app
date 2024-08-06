package com.example.foodapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.foodapp.Adapter.CartAdapter;
import com.example.foodapp.Database.CloudFunctions;
import com.example.foodapp.Domain.Notification;
import com.example.foodapp.Domain.Shipper;
import com.example.foodapp.Listener.RefreshCartListener;
import com.example.foodapp.Domain.FoodCart;
import com.example.foodapp.Domain.Order;
import com.example.foodapp.Domain.User;
import com.example.foodapp.databinding.ActivityCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CartActivity extends BaseActivity implements RefreshCartListener {

    ActivityCartBinding binding;
    private ArrayList<FoodCart> listFood;

    private Double subTotalPrice;
    private Double deliveryTaxPrice;
    private Double totalTaxPrice;
    private Double totalPrice;

    private Order order;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a dd/MM/yyyy");

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCartBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());
        initList();
        updateTotalCost();
        setButton();
        binding.noItemView.setVisibility(View.GONE);

        DatabaseReference myRef = database.getReference("Cart");
        myRef.child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateTotalCost();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateTotalCost();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                updateTotalCost();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initList() {
        DatabaseReference myRef = database.getReference("Cart");
        ArrayList<FoodCart> list = new ArrayList<>();
        myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(FoodCart.class));
                    }
                if (list.size() > 0){
                    listFood = list;
                    binding.cartView.setLayoutManager(new LinearLayoutManager(CartActivity.this, LinearLayoutManager.VERTICAL, false));
                    RecyclerView.Adapter adapter = new CartAdapter(list, CartActivity.this);
                    binding.cartView.setAdapter(adapter);
                }
                } else binding.noItemView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateTotalCost(){
        DatabaseReference myRef = database.getReference("Cart");
        ArrayList<FoodCart> list = new ArrayList<>();



        myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                       list.add(issue.getValue(FoodCart.class));
                    }
                    double subTotal = 0;
                    double deliveryTax = 1.99;
                    double totalTax = 0;
                    double total = 0;


                    if (list.size() > 0){
                        for (FoodCart foodCart : list){
                            subTotal += foodCart.getFood().getPrice() * foodCart.getQuantity();
                        }
                    }




                    totalTax = (subTotal + deliveryTax)*0.1;
                    total = subTotal + deliveryTax + totalTax;


                    subTotalPrice = subTotal;
                    deliveryTaxPrice = deliveryTax;
                    totalTaxPrice = totalTax;
                    totalPrice = total;

                    DecimalFormat df = new DecimalFormat("#.##");
                    binding.subtotalTxt.setText("$" + df.format(subTotal));
                    binding.deliveryTxt.setText("$" + df.format(deliveryTax));
                    binding.totaltaxTxt.setText("$" + df.format(totalTax));
                    binding.totalTxt.setText("$" + df.format(total));

                    binding.orderBtn.setText("Place order ($" + df.format(total) + ")");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setButton(){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.shopNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        binding.orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderFoods();
                Intent intent1 = new Intent(CartActivity.this, OrderActivity.class);
                startActivity(intent1);

                Intent intent = new Intent("com.android.UPDATE_HAVE_ORDER");
                intent.putExtra("isHaveOrder", true);
                sendBroadcast(intent);
                isHaveOrderNow = true;

                Timer timer = new Timer();

                TimerTask task1 = new TimerTask() {
                    @Override
                    public void run() {
                        getReady();
                    }
                };

                TimerTask task2 = new TimerTask() {
                    @Override
                    public void run() {
                        getShipper();
                    }
                };

                TimerTask task3 = new TimerTask() {
                    @Override
                    public void run() {
                        getFinished();
                    }
                };

                timer.schedule(task1, 3000);
                timer.schedule(task2, 18000);
                timer.schedule(task3, 30000);

            }
        });
    }

    private void orderFoods() {
        database.getReference("User").orderByChild("uid")
                .equalTo(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot issue: snapshot.getChildren()){
                                User user = issue.getValue(User.class);
                                order = new Order();

                                order.create(listFood, user, subTotalPrice, deliveryTaxPrice, totalTaxPrice, totalPrice);

                                database.getReference("Order").child(mAuth.getCurrentUser().getUid()).setValue(order);
                                Toast.makeText(CartActivity.this, "Place order successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void getReady(){
        order.setReadyTime(dateFormat.format(new Date()));
        order.setStatus(1);

        database.getReference("Order").child(uid).setValue(order);

        Notification.addNotificationToFirebase(order, "Ready!", "Your order is ready!");
        CloudFunctions.callNotificationFunction(uid, "ready");

        Intent intent = new Intent("com.android.ORDER_STATUS_UPDATE");
        intent.putExtra("orderStatus", "ready");
        sendBroadcast(intent);
    }

    public void getShipper(){
        database.getReference("Shipper").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    boolean hasShipper = false;
                    for (DataSnapshot issue: snapshot.getChildren()){
                        if (issue.getValue(Shipper.class).getStatus().equalsIgnoreCase("ready") && !hasShipper){
                            hasShipper = true;
                            CloudFunctions.callNotificationFunction(uid, "shipping");

                            Shipper shipper = issue.getValue(Shipper.class);
                            order.setShipper(shipper);
                            order.setStatus(2);
                            order.setShippingTime(dateFormat.format(new Date()));
                            database.getReference("Order").child(uid).setValue(order);

                            Intent intent = new Intent("com.android.ORDER_STATUS_UPDATE");
                            intent.putExtra("orderStatus", "shipping");
                            intent.putExtra("shipperName", shipper.getFirstName() + " " + shipper.getLastName());
                            intent.putExtra("rating", String.valueOf(shipper.getRatings()));
                            sendBroadcast(intent);

                            Notification.addNotificationToFirebase(order, "Shipping!", "Your order is being shipped!");
                        }
                    }
                }
                else {
                    CloudFunctions.callNotificationFunction(uid, "shipping_failed");
                    Notification.addNotificationToFirebase(order, "Shipping failed!", "Cannot find shipper for your order");
                };
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getFinished(){
        order.setFinishTime(dateFormat.format(new Date()));
        order.setStatus(3);

        database.getReference("Order").child(uid).setValue(order);
        CloudFunctions.callNotificationFunction(uid, "finished");

        Notification.addNotificationToFirebase(order, "Finished!", "Your order is finished!");
        database.getReference("ReviewOrder").child(uid).child(order.getId()).setValue(order);

        database.getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        Intent intent = new Intent("com.android.UPDATE_HAVE_ORDER");
        intent.putExtra("isHaveOrder", false);
        sendBroadcast(intent);
        isHaveOrderNow = false;


        Intent intent1 = new Intent("com.android.ORDER_STATUS_UPDATE");
        intent1.putExtra("orderStatus", "finished");
        sendBroadcast(intent1);
    }



    @Override
    public void onClick() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

    }

    @Override
    public void onUpdate() {

    }
}