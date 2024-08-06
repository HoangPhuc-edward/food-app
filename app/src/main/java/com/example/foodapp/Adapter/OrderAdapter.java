package com.example.foodapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodapp.Domain.FoodCart;
import com.example.foodapp.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.viewholder> {

    Context context;
    ArrayList<FoodCart> items;

    public OrderAdapter(ArrayList<FoodCart> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_order, parent, false);
        return new viewholder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getFood().getTitle());

        String quantity = "Quantity: ";
        holder.quantityTxt.setText(quantity + items.get(position).getQuantity());

        String price = "$" + items.get(position).getQuantity() * items.get(position).getFood().getPrice();
        holder.priceTxt.setText(price);

        Glide.with(context)
                .load(items.get(position).getFood().getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        TextView titleTxt, quantityTxt, priceTxt;
        ImageView img;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            quantityTxt = itemView.findViewById(R.id.quantityTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            img = itemView.findViewById(R.id.img);
        }
    }
}
