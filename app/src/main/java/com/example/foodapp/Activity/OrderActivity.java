package com.example.foodapp.Activity;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.example.foodapp.Adapter.OrderAdapter;
import com.example.foodapp.Database.CloudFunctions;
import com.example.foodapp.Domain.Notification;
import com.example.foodapp.Domain.Order;
import com.example.foodapp.Domain.Shipper;
import com.example.foodapp.Domain.Time;
import com.example.foodapp.Utils.DialogHelper;
import com.example.foodapp.databinding.ActivityOrderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.ChronoLocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class OrderActivity extends BaseActivity {

    ActivityOrderBinding binding;
    private Order order;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a dd/MM/yyyy");

    private boolean isReview;
    private String orderId;

    private BroadcastReceiver orderStatusReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());

        getIntentVariables();
        binding.cancelBtn.setEnabled(true);


        orderStatusReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                String orderStatus = intent.getStringExtra("orderStatus");
                String shipperName = intent.getStringExtra("shipperName");
                String rating = intent.getStringExtra("rating");

                if (Objects.equals(orderStatus, "ready")){
                    binding.processBarOrder.setProgress(15);
                    binding.readyTxt.setText(timeFormat.format(new Date()));
                }
                else if (Objects.equals(orderStatus, "shipping")){
                    binding.shipperTxt.setText(shipperName + " (" + rating + " ⭐" + ")");
                    binding.processBarOrder.setProgress(50);
                    binding.cancelBtn.setEnabled(false);
                    binding.shippingTxt.setText(timeFormat.format(new Date()));
                }
                else if (Objects.equals(orderStatus, "finished")){
                    binding.processBarOrder.setProgress(100);
                    binding.finishedTxt.setText(timeFormat.format(new Date()));
                }
                else {
                    binding.processBarOrder.setProgress(0);
                }

            }
        };

        IntentFilter filter = new IntentFilter("com.android.ORDER_STATUS_UPDATE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(orderStatusReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }

        if (isReview){
            initListReviewOrder();
            binding.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        else {
            initListCurrentOrder();
            binding.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

        if (!isReview){
            OnBackPressedDispatcher onBackPressedDispatcher =  getOnBackPressedDispatcher();
            onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(orderStatusReceiver);
    }




    private void getIntentVariables() {
        isReview = getIntent().getBooleanExtra("isReview", false);
        orderId = getIntent().getStringExtra("orderId");
    }

    private void initListCurrentOrder() {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Order").child(uid);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    order = snapshot.getValue(Order.class);
                    binding.orderView.setLayoutManager(new LinearLayoutManager(OrderActivity.this, LinearLayoutManager.VERTICAL, false));
                    RecyclerView.Adapter adapter = new OrderAdapter(order.getFoodCarts());
                    binding.orderView.setAdapter(adapter);

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd/MM/yyyy");

                    try {
                        Date createTime = simpleDateFormat.parse(order.getCreateTime());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(createTime);
                        calendar.add(Calendar.MINUTE, 30);

                        Date newDate = calendar.getTime();

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                        binding.estimateTxt.setText(format.format(newDate));

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }


                    DecimalFormat df = new DecimalFormat("#.##");
                    binding.subtotalTxt.setText("$" + df.format(order.getSubTotal()));
                    binding.deliveryTxt.setText("$" + df.format(order.getDeliveryTax()));
                    binding.totaltaxTxt.setText("$" + df.format(order.getTotalTax()));
                    binding.totalTxt.setText("$" + df.format(order.getTotal()));

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm a");

                    if (order.getStatus() >= 1){
                        binding.readyTxt.setText(order.getReadyTime());
                        binding.processBarOrder.setProgress(15);
                    }

                    if (order.getStatus() >= 2){
                        binding.shippingTxt.setText(order.getReadyTime());
                        binding.processBarOrder.setProgress(50);
                    }

                    if (order.getStatus() >= 3){
                        binding.finishedTxt.setText(order.getReadyTime());
                        binding.processBarOrder.setProgress(100);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initListReviewOrder(){
        binding.cancelBtn.setVisibility(View.GONE);
        binding.processBarOrder.setProgress(100);

        database.getReference("ReviewOrder").child(uid).child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    order = snapshot.getValue(Order.class);
                    binding.orderView.setLayoutManager(new LinearLayoutManager(OrderActivity.this, LinearLayoutManager.VERTICAL, false));
                    RecyclerView.Adapter adapter = new OrderAdapter(order.getFoodCarts());
                    binding.orderView.setAdapter(adapter);

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd/MM/yyyy");

                    try {
                        Date createTime = simpleDateFormat.parse(order.getCreateTime());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(createTime);
                        calendar.add(Calendar.MINUTE, 30);

                        Date newDate = calendar.getTime();

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                        binding.estimateTxt.setText(format.format(newDate));

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }


                    DecimalFormat df = new DecimalFormat("#.##");
                    binding.subtotalTxt.setText("$" + df.format(order.getSubTotal()));
                    binding.deliveryTxt.setText("$" + df.format(order.getDeliveryTax()));
                    binding.totaltaxTxt.setText("$" + df.format(order.getTotalTax()));
                    binding.totalTxt.setText("$" + df.format(order.getTotal()));


                    Shipper shipper = order.getShipper();
                    String shipperName = shipper.getFirstName() + " " + shipper.getLastName();
                    String rating = shipper.getRatings() + " ⭐";
                    binding.shipperTxt.setText(shipperName + " (" + rating + ")");

                    binding.readyTxt.setText(order.getReadyTime());
                    binding.shippingTxt.setText(order.getShippingTime());
                    binding.finishedTxt.setText(order.getFinishTime());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @SuppressLint("SetTextI18n")
    public void getCancel(){
        order.setCancelTime(dateFormat.format(new Date()));
        binding.processBarOrder.setProgress(100);
        binding.finishedTxt.setText(timeFormat.format(new Date()));
        binding.finishTitle.setText("Cancelled");

        database.getReference("Order").child(uid).setValue(order);
        DialogHelper.showNotifyPopup(this, "Your order has been cancelled", "You have successfully cancel orders", "success");

        Notification.addNotificationToFirebase(order, "Cancelled!", "Your order is cancelled!");
    }

}