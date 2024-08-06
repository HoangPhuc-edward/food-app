package com.example.foodapp.Database;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.foodapp.Activity.MainActivity;
import com.example.foodapp.Domain.FoodCart;
import com.example.foodapp.Domain.Foods;
import com.example.foodapp.Domain.Location;
import com.example.foodapp.Domain.Price;
import com.example.foodapp.Domain.Time;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseHelper {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private static DatabaseHelper instance;

    private DatabaseHelper(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    public static DatabaseHelper getInstance(){
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper();
                }
            }
        }
        return instance;
    }

    public void addFoodToCart(Foods foods, int quantity){
        FoodCart foodCart = new FoodCart(foods, quantity);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Cart");

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef.child(uid).child(String.valueOf(foodCart.getFood().getId())).setValue(foodCart);
    }

    public void addMoreFoodToCart(Foods foods){
        DatabaseReference value = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        value.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int quantity = 0;
                    for (DataSnapshot issue: snapshot.getChildren()){
                        FoodCart value = issue.getValue(FoodCart.class);
                        if (value.getFood().getId() == foods.getId())
                            quantity = value.getQuantity();
                    }

                    addFoodToCart(foods, quantity + 1);
                }
                else addFoodToCart(foods, 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateNumberInCart(TextView numCartTxt){
        database.getReference("Cart").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long num = snapshot.getChildrenCount();
                if (num > 0) {
                    numCartTxt.setText(String.valueOf(num));
                    numCartTxt.setVisibility(View.VISIBLE);
                }
                else numCartTxt.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
