package com.example.foodapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.foodapp.Activity.DetailFoodActivity;
import com.example.foodapp.Adapter.FoodListAdapter;
import com.example.foodapp.Listener.FragmentListener;
import com.example.foodapp.Listener.ListFoodListener;
import com.example.foodapp.Domain.Foods;
import com.example.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteFragment extends Fragment implements ListFoodListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentListener listener;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public FavoriteFragment(FragmentListener listener){
        this.listener = listener;
    }


    private RecyclerView favoriteView;
    private FoodListAdapter favoriteAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    private void initList(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<Foods> list = new ArrayList<>();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Favorite").child(uid);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Foods.class));
                    }
                    favoriteAdapter = new FoodListAdapter(list, FavoriteFragment.this, true);
                    favoriteView.setAdapter(favoriteAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        container.requestDisallowInterceptTouchEvent(true);

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        View view1 = view;



        // Initialize RecyclerView
        favoriteView = view.findViewById(R.id.favoriteView);
        favoriteView.setLayoutManager(new GridLayoutManager(getContext(), 2));


        initList();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Favorite").child(uid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        initList();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        initList();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        initList();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        initList();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        ImageView btn = view.findViewById(R.id.editSearchBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editSearchTxt = view1.findViewById(R.id.editTextSearchFavorite);
                String searchText = editSearchTxt.getText().toString();


                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ArrayList<Foods> list = new ArrayList<>();
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Favorite").child(uid);

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot issue: snapshot.getChildren()){
                                if (issue.getValue(Foods.class).getTitle().toLowerCase().contains(searchText.toLowerCase())){
                                    list.add(issue.getValue(Foods.class));
                                }

                            }
                            favoriteAdapter = new FoodListAdapter(list, FavoriteFragment.this, true);
                            favoriteView.setAdapter(favoriteAdapter);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        return view;
    }



    @Override
    public void navigateToDetailFood(Foods food) {
        Intent intent = new Intent(getActivity(), DetailFoodActivity.class);
        intent.putExtra("FoodId", food.getId());
        startActivity(intent);
    }

    @Override
    public void startAddFoodAnimation(Foods foods, int x, int y) {
        listener.onClick1();
    }
}