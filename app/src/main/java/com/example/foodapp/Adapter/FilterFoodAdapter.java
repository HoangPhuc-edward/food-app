package com.example.foodapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodapp.Domain.Foods;
import com.example.foodapp.Listener.ListFoodListener;
import com.example.foodapp.R;

import java.util.ArrayList;

public class FilterFoodAdapter extends RecyclerView.Adapter<FilterFoodAdapter.viewholder> {

    ArrayList<Foods> foods;

    Context context;

    private ListFoodListener listener;

    public FilterFoodAdapter(ArrayList<Foods> foods, ListFoodListener listener) {
        this.foods = foods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterFoodAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter_food, parent, false);
        return new viewholder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FilterFoodAdapter.viewholder holder, int position) {
        holder.nameTxt.setText(foods.get(position).getTitle());
        holder.priceTxt.setText("$"+ foods.get(position).getPrice());


        holder.btn.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            int[] location = new int[2];
            holder.btn.getLocationOnScreen(location);
            listener.startAddFoodAnimation(foods.get(pos), location[0], location[1]);
        });

        holder.container.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            listener.navigateToDetailFood(foods.get(pos));
        });

        Glide.with(context)
                .load(foods.get(position).getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(14))
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{

        TextView nameTxt, priceTxt, btn;
        ImageView img;

        ConstraintLayout container;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.filterFoodNameTxt);
            priceTxt = itemView.findViewById(R.id.filterFoodPriceTxt);
            img = itemView.findViewById(R.id.filterFoodImg);
            btn = itemView.findViewById(R.id.filterFoodAddBtn);
            container = itemView.findViewById(R.id.filterFoodContainer);
        }
    }
}
