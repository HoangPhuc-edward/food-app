package com.example.foodapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodapp.Utils.AnimationHelper;
import com.example.foodapp.Domain.FoodCart;
import com.example.foodapp.Domain.Foods;

import com.example.foodapp.databinding.ActivityDetailFoodBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class DetailFoodActivity extends BaseActivity {

    private int foodId;
    private Foods food;
    private ActivityDetailFoodBinding binding;

    private BroadcastReceiver cartReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailFoodBinding.inflate(getLayoutInflater());

        getFoodId();
        initDetail();

        setButton();


        updateTotalPrice(0);
        addFoodToCartEvent();
        addCountEvent();
        setContentView(binding.getRoot());



        cartReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isHaveOrder = intent.getBooleanExtra("isHaveOrder", false);
                updateCartBtnFunction(isHaveOrder);
            }
        };

        IntentFilter filter = new IntentFilter("com.android.UPDATE_HAVE_ORDER");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(cartReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }

        database.getReference("Cart").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                databaseHelper.updateNumberInCart(binding.numCartTxt);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                databaseHelper.updateNumberInCart(binding.numCartTxt);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.redPointView.setVisibility(View.GONE);

        String uid = mAuth.getCurrentUser().getUid();
        database.getReference("Favorite").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(String.valueOf(foodId))){
                    binding.favoriteBtn.setColorFilter(Color.parseColor("#FFC90E"), PorterDuff.Mode.MULTIPLY);
                }
                else binding.favoriteBtn.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cartReceiver);
    }


    private void updateCartBtnFunction(boolean isHaveOrder){
        if (isHaveOrder){
            binding.cartImg.setOnClickListener(view -> {
                Intent intent1 = new Intent(DetailFoodActivity.this, OrderActivity.class);
                startActivity(intent1);
            });
            binding.numCartTxt.setVisibility(View.GONE);
        }
        else {
            binding.cartImg.setOnClickListener(view -> {
                Intent intent2 = new Intent(DetailFoodActivity.this, CartActivity.class);
                startActivity(intent2);
            });
            databaseHelper.updateNumberInCart(binding.numCartTxt);
        }
    }

    private void setButton(){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateCartBtnFunction(isHaveOrderNow);

        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uid = mAuth.getCurrentUser().getUid();
                database.getReference("Favorite").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isFavorite = false;

                        if (snapshot.exists()) isFavorite = snapshot.hasChild(String.valueOf(foodId));

                        if (isFavorite){
                            binding.favoriteBtn.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                            removeFavorite();
                            Toast.makeText(DetailFoodActivity.this, "Removed from favorite!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Add to favorite
                            binding.favoriteBtn.setColorFilter(Color.parseColor("#FFC90E"), PorterDuff.Mode.MULTIPLY);
                            addFavorite();
                            Toast.makeText(DetailFoodActivity.this, "Added to favorite!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }




    private void addFavorite(){
        DatabaseReference myRef = database.getReference("Foods");
        String mUid = mAuth.getCurrentUser().getUid();

        myRef.orderByChild("Id").equalTo(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    boolean isAdded = false;
                    for (DataSnapshot issue: snapshot.getChildren()){
                        if (!isAdded){
                            database.getReference("Favorite").child(mUid).child(String.valueOf(foodId)).setValue(food);

                            isAdded = true;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeFavorite(){
        String uid = mAuth.getCurrentUser().getUid();
        database.getReference("Favorite").child(uid).child(String.valueOf(foodId)).removeValue();
    }

    private void initDetail() {
        DatabaseReference myRef = database.getReference("Foods");
        Query query = myRef.orderByChild("Id").equalTo(foodId);

        binding.detailProcessBar.setVisibility(View.VISIBLE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        food = issue.getValue(Foods.class);
                        binding.titleTxt.setText(food.getTitle());
                        binding.detailTxt.setText(food.getDescription());
                        binding.ratingBar.setRating((float) food.getStar());
                        binding.ratingTxt.setText(food.getStar() + " rating");
                        binding.timeTxt.setText(food.getTimeValue() + " min");
                        binding.priceTxt.setText("$" + food.getPrice());
                        Glide.with(DetailFoodActivity.this)
                                .load(food.getImagePath())
                                .into(binding.pic);
                        binding.detailProcessBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFoodId() {
        foodId = getIntent().getIntExtra("FoodId", 0);
    }


    private void addFoodToCartEvent(){
        AppCompatButton button = binding.addBtn;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(binding.countTxt.getText().toString());

                if (quantity > 0){
                    FoodCart foodCart = new FoodCart(food, quantity);
                    DatabaseReference myRef = database.getReference("Cart");

                    String uid = mAuth.getCurrentUser().getUid();
                    myRef.child(uid).child(String.valueOf(foodCart.getFood().getId())).setValue(foodCart);

                    AnimationHelper animationHelper = new AnimationHelper(binding.redPointView, binding.countTxt, binding.cartImg);
                    animationHelper.startAnimation(1000);

                    Toast.makeText(DetailFoodActivity.this, "Added successfully!", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateTotalPrice(int count){
        if (count == 0){
            binding.totalTxt.setText("$0");
            return;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        binding.totalTxt.setText("$" + df.format(food.getPrice()*count));
    }

    private void addCountEvent(){
        ImageView minusBtn = binding.minusBtn;
        ImageView plusBtn = binding.plusBtn;
        TextView countTxt = binding.countTxt;

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(countTxt.getText().toString());
                if (value > 0){
                    countTxt.setText(String.valueOf(value - 1));
                    updateTotalPrice(value-1);
                }
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(countTxt.getText().toString());
                if (value < 50){
                    countTxt.setText(String.valueOf(value + 1));
                    updateTotalPrice(value+1);
                }
                else Toast.makeText(DetailFoodActivity.this, "The maximum quantity is 50!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    

}