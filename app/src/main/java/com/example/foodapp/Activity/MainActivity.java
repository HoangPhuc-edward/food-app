package com.example.foodapp.Activity;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.foodapp.Adapter.BestFoodAdapter;
import com.example.foodapp.Adapter.CategoryAdapter;
import com.example.foodapp.Adapter.FilterFoodAdapter;
import com.example.foodapp.Listener.FragmentListener;
import com.example.foodapp.Listener.ListFoodListener;
import com.example.foodapp.Listener.CategoryListener;
import com.example.foodapp.Utils.AnimationHelper;
import com.example.foodapp.Domain.Category;
import com.example.foodapp.Domain.Foods;
import com.example.foodapp.Domain.Location;
import com.example.foodapp.Domain.Price;
import com.example.foodapp.Domain.Time;
import com.example.foodapp.Domain.User;
import com.example.foodapp.Fragment.FavoriteFragment;
import com.example.foodapp.Fragment.NotificationFragment;
import com.example.foodapp.R;
import com.example.foodapp.Fragment.UserFragment;
import com.example.foodapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements CategoryListener, ListFoodListener, FragmentListener {
    private ActivityMainBinding binding;

    private BroadcastReceiver cartReceiver;
    private User currentUser;

    public static int REQUEST_CODE = 100;

    private int locationId = 2;

    private int timeId = 3;
    private int priceId = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setButtons();

        askNotificationPermission();
        initDropdownData();

        initFilterFoods();

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        initBestFoods();
        initCategory();

        updateUsername();

        databaseHelper.updateNumberInCart(binding.numCartTxt);

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

        binding.navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    hideAllFragments();

                    return true;
                } else if (id == R.id.navigation_favorite) {

                    replaceFragment(new FavoriteFragment(MainActivity.this));
                    return true;
                } else if (id == R.id.navigation_notification) {
                    replaceFragment(new NotificationFragment());
                    return true;
                } else if (id == R.id.navigation_user) {
                    replaceFragment(new UserFragment(currentUser));
                    return true;
                }

                return false;
            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cartReceiver);
    }

    public void updateCartBtnFunction(boolean isHaveOrder){
        if (isHaveOrder){
            binding.cartBtn.setOnClickListener(view -> {
                Intent intent1 = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent1);
            });
            binding.numCartTxt.setVisibility(View.GONE);
        }
        else {
            binding.cartBtn.setOnClickListener(view -> {
                Intent intent2 = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent2);
            });
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                    Toast.makeText(this, "Your app will not show notifications", Toast.LENGTH_SHORT).show();
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.POST_NOTIFICATIONS
                }, REQUEST_CODE);
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }




    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private void hideAllFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null && fragment.isVisible()) {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

    private void setButtons(){
        //Sign out
        binding.signOutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        //Navigate to search
        binding.searchBtn.setOnClickListener(view -> {

            String searchText = binding.searchEditText.getText().toString().trim();
            Intent intent = new Intent(MainActivity.this, ListFoodActivity.class);
            intent.putExtra("CategoryId", 0);
            intent.putExtra("Category", "Search Result");
            intent.putExtra("text", searchText);
            intent.putExtra("isSearch", true);

            startActivity(intent);
        });

        //Navigate to cart
        updateCartBtnFunction(isHaveOrderNow);
    }

    private void initFilterFoods() {
        filterFood();
    }

    private boolean checkFood(Foods food){
        if (locationId == 2 || locationId == food.getLocationId())
            if (priceId == 3 || priceId == food.getPriceId())
                return timeId == 3 || timeId == food.getTimeId();

        return false;
    }


    private void filterFood(){
        binding.filterFoodProcessBar.setVisibility(View.VISIBLE);
        binding.unfoundTxt.setVisibility(View.GONE);
        ArrayList<Foods> list = new ArrayList<>();
        database.getReference("Foods").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        Foods food = issue.getValue(Foods.class);
                        if (checkFood(food)) list.add(issue.getValue(Foods.class));
                    }

                    binding.filterFoodProcessBar.setVisibility(View.GONE);

                    if (list.size() > 0){
                        binding.filterFoodView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        RecyclerView.Adapter adapter = new FilterFoodAdapter(list, MainActivity.this);
                        binding.filterFoodView.setAdapter(adapter);

                    }
                    else binding.unfoundTxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initDropdownData(){
        ArrayList<Location> locations = new ArrayList<>();
        ArrayList<Time> times = new ArrayList<>();
        ArrayList<Price> prices = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()) {
                        locations.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter1 = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, locations);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.locationSp.setAdapter(adapter1);

                    binding.locationSp.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.locationSp.setSelection(2);
                        }
                    });

                    binding.locationSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            locationId = i;
                            filterFood();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        times.add(issue.getValue(Time.class));
                    }
                }
                ArrayAdapter<Time> adapter2 = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, times);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.timeSp.setAdapter(adapter2);

                binding.timeSp.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.timeSp.setSelection(3);
                    }
                });

                binding.timeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        timeId = i;
                        filterFood();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference("Price").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        prices.add(issue.getValue(Price.class));
                    }
                    ArrayAdapter<Price> adapter3 = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, prices);
                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.priceSp.setAdapter(adapter3);

                    binding.priceSp.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.priceSp.setSelection(3);
                        }
                    });

                    binding.priceSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            priceId = i;
                            filterFood();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void initBestFoods() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBarBestFood.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<Foods>();
        Query query = myRef.orderByChild("BestFood").equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot issue: snapshot.getChildren()){
                    list.add(issue.getValue(Foods.class));
                }
                if (list.size() > 0){
                    binding.bestFoodView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    RecyclerView.Adapter adapter = new BestFoodAdapter(list, MainActivity.this);
                    binding.bestFoodView.setAdapter(adapter);
                }
                binding.progressBarBestFood.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("Category");
        binding.progressBarBestFood.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot issue: snapshot.getChildren()){
                    list.add(issue.getValue(Category.class));
                }
                if (list.size() > 0){
                    binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
                    RecyclerView.Adapter adapter = new CategoryAdapter(list, MainActivity.this);


                    binding.categoryView.setAdapter(adapter);
                }
                binding.progressBarCategory.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void updateUsername(){
        String mUid = mAuth.getCurrentUser().getUid();

        Query query = database.getReference("User").orderByChild("uid").equalTo(mUid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        binding.usernameTxt.setText(issue.getValue(User.class).getLastName());
                        currentUser = issue.getValue(User.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    @Override
    public void onCategoryClicked(Category category) {
        Intent intent = new Intent(MainActivity.this, ListFoodActivity.class);

        intent.putExtra("CategoryId", category.getId());
        intent.putExtra("Category", category.getName());
        intent.putExtra("text", "hello");
        intent.putExtra("isSearch", false);

        startActivity(intent);
    }

    @Override
    public void navigateToDetailFood(Foods food) {
        Intent intent = new Intent(MainActivity.this, DetailFoodActivity.class);
        intent.putExtra("FoodId", food.getId());
        startActivity(intent);
    }

    @Override
    public void startAddFoodAnimation(Foods foods, int x1, int y1) {

        databaseHelper.addMoreFoodToCart(foods);

        Toast.makeText(MainActivity.this, "Added successfully!", Toast.LENGTH_SHORT).show();

        AnimationHelper animationHelper = new AnimationHelper(binding.redPointView, x1, y1, binding.cartBtn);
        animationHelper.startAnimation(1000);

        binding.numCartTxt.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick1() {
        replaceFragment(new FavoriteFragment(MainActivity.this));
    }

    @Override
    public void onClick2() {

    }

    @Override
    public void onClick3() {

    }

}