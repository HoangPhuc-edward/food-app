package com.example.foodapp.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.foodapp.Adapter.FoodListAdapter;
import com.example.foodapp.Listener.ListFoodListener;
import com.example.foodapp.Domain.Foods;
import com.example.foodapp.databinding.ActivityListFoodBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListFoodActivity extends BaseActivity implements ListFoodListener {

    ActivityListFoodBinding binding;
    private RecyclerView.Adapter adapterListFood;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        getIntentExtra();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initList();

    }



    private void initList() {
        DatabaseReference myRef = database.getReference("Foods");
        ArrayList<Foods> list = new ArrayList<>();
        binding.progressBar.setVisibility(View.VISIBLE);

        Query query;
        if (isSearch){
            query = myRef;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue: snapshot.getChildren()){
                            if (issue.getValue(Foods.class).getTitle().toLowerCase().contains(searchText.toLowerCase())){
                                list.add(issue.getValue(Foods.class));
                            }
                        }
                    }
                    if (list.size() > 0){
                        binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodActivity.this, 2));
                        RecyclerView.Adapter adapter = new FoodListAdapter(list, ListFoodActivity.this, false);
                        binding.foodListView.setAdapter(adapter);

                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            query = myRef.orderByChild("CategoryId").equalTo(categoryId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue: snapshot.getChildren()){

                            list.add(issue.getValue(Foods.class));
                        }
                    }
                    if (list.size() > 0){
                        binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodActivity.this, 2));
                        RecyclerView.Adapter adapter = new FoodListAdapter(list, ListFoodActivity.this, false);
                        binding.foodListView.setAdapter(adapter);

                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("Category");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);

        binding.titleTxt.setText(categoryName);

    }

    @Override
    public void navigateToDetailFood(Foods food) {
        Intent intent = new Intent(ListFoodActivity.this, DetailFoodActivity.class);
        intent.putExtra("FoodId", food.getId());
        startActivity(intent);
    }

    @Override
    public void startAddFoodAnimation(Foods foods, int x, int y) {

    }
}