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
import com.example.foodapp.Listener.RefreshCartListener;
import com.example.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewholder> {

    ArrayList<FoodCart> items;
    Context context;

    RefreshCartListener listener;

    public CartAdapter(ArrayList<FoodCart> foodCart, RefreshCartListener listener) {
        this.items = foodCart;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_cart, parent, false);
        return new viewholder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.viewholder holder, int position) {

        holder.titleTxt.setText(items.get(position).getFood().getTitle());
        holder.quantityTxt.setText("Quantity: " + items.get(position).getQuantity() + " x " + items.get(position).getFood().getPrice());
        holder.countTxt.setText(String.valueOf(items.get(position).getQuantity()));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DecimalFormat df = new DecimalFormat("#.##");

        holder.totalTxt.setText("$" + df.format(items.get(position).getFood().getPrice() * items.get(position).getQuantity()));
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(holder.countTxt.getText().toString());
                int myPosition = holder.getAdapterPosition();
                if (value > 1){
                    value--;
                    holder.countTxt.setText(String.valueOf(value));
                    holder.quantityTxt.setText("Quantity: " + value + " x " + items.get(myPosition).getFood().getPrice());
                    holder.totalTxt.setText("$" + df.format(items.get(myPosition).getFood().getPrice() * value));

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Cart").child(mAuth.getCurrentUser().getUid());
                    items.get(myPosition).setQuantity(value);
                    myRef.child(String.valueOf(items.get(myPosition).getFood().getId())).setValue(items.get(myPosition));
                }
            }
        });

        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.parseInt(holder.countTxt.getText().toString());
                int myPosition = holder.getAdapterPosition();
                if (value < 50){
                    value++;
                    holder.countTxt.setText(String.valueOf(value));
                    holder.quantityTxt.setText("Quantity: " + value + " x " + items.get(myPosition).getFood().getPrice());
                    holder.totalTxt.setText("$" + df.format(items.get(myPosition).getFood().getPrice() * value));

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Cart").child(mAuth.getCurrentUser().getUid());
                    items.get(myPosition).setQuantity(value);
                    myRef.child(String.valueOf(items.get(myPosition).getFood().getId())).setValue(items.get(myPosition));
                }
            }
        });

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int myPosition = holder.getAdapterPosition();
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Cart").child(mAuth.getCurrentUser().getUid());
                myRef.child(String.valueOf(items.get(myPosition).getFood().getId())).removeValue();
                listener.onUpdate();
                listener.onClick();
            }
        });

        Glide.with(context)
                .load(items.get(position).getFood().getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt, quantityTxt;
        ImageView pic;
        ImageView minusBtn, plusBtn, removeBtn;
        TextView countTxt, totalTxt;



        public viewholder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            quantityTxt = itemView.findViewById(R.id.quantityTxt);
            pic = itemView.findViewById(R.id.img);

            minusBtn = itemView.findViewById(R.id.minusBtn);
            plusBtn = itemView.findViewById(R.id.plusBtn);
            countTxt = itemView.findViewById(R.id.countTxt);
            totalTxt = itemView.findViewById(R.id.totalTxt);

            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }


}
