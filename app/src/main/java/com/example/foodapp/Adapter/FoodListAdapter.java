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
import com.example.foodapp.Domain.Foods;
import com.example.foodapp.Listener.ListFoodListener;
import com.example.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.viewholder> {
    ArrayList<Foods> items;
    Context context;

    private ListFoodListener listener;
    private boolean isFavorite;
    public FoodListAdapter(ArrayList<Foods> items, ListFoodListener listener, boolean isFavorite) {
        this.items = items;
        this.listener = listener;
        this.isFavorite = isFavorite;
    }

    @NonNull
    @Override
    public FoodListAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_food, parent, false);
        return new viewholder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FoodListAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.timeTxt.setText(items.get(position).getTimeValue() + " min");
        holder.priceTxt.setText("$" + items.get(position).getPrice());
        holder.rateTxt.setText(String.valueOf(items.get(position).getStar()));

        if (isFavorite){
            holder.removeTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int foodId = items.get(holder.getAdapterPosition()).getId();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference("Favorite").child(uid).child(String.valueOf(foodId)).removeValue();
                    listener.startAddFoodAnimation(new Foods(), 1, 1);
                }
            });
        }
        else {
            holder.removeTxt.setVisibility(View.GONE);
        }


        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    listener.navigateToDetailFood(items.get(adapterPosition));
                }
            }
        });

        Glide.with(context)
                .load(items.get(position).getImagePath())
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{
        TextView titleTxt, timeTxt, priceTxt, rateTxt, removeTxt;
        ConstraintLayout container;
        ImageView pic;
        public viewholder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            rateTxt = itemView.findViewById(R.id.rateTxt);

            pic = itemView.findViewById(R.id.img);
            container = itemView.findViewById(R.id.foodListContainer);

            removeTxt = itemView.findViewById(R.id.removeBtn);
        }
    }
}
