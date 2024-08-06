package com.example.foodapp.Adapter;



import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.Domain.Category;
import com.example.foodapp.Listener.CategoryListener;
import com.example.foodapp.R;

import java.util.ArrayList;




public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder> {
    ArrayList<Category> items;
    Context context;
    private CategoryListener listener;

    public CategoryAdapter(ArrayList<Category> items, CategoryListener listener) {
        this.items = items;
        this.listener = listener;

    }

    @NonNull
    @Override
    public CategoryAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
        return new viewholder(inflate);
    }




    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getName());

        switch (position){
            case 0: {
                holder.pic.setBackgroundResource(R.drawable.cat_0_background);
                break;
            }
            case 1: {
                holder.pic.setBackgroundResource(R.drawable.cat_1_background);
                break;
            }
            case 2: {
                holder.pic.setBackgroundResource(R.drawable.cat_2_background);
                break;
            }
            case 3: {
                holder.pic.setBackgroundResource(R.drawable.cat_3_background);
                break;
            }
            case 4: {
                holder.pic.setBackgroundResource(R.drawable.cat_4_background);
                break;
            }
            case 5: {
                holder.pic.setBackgroundResource(R.drawable.cat_5_background);
                break;
            }
            case 6: {
                holder.pic.setBackgroundResource(R.drawable.cat_6_background);
                break;
            }
            case 7: {
                holder.pic.setBackgroundResource(R.drawable.cat_7_background);
                break;
            }

        }

        int drawableResourceId = context.getResources().getIdentifier(items.get(position).getImagePath(),
                "drawable", holder.itemView.getContext().getPackageName());




        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    listener.onCategoryClicked(items.get(adapterPosition));
                }
            }
        });

        Glide.with(context)
                .load(drawableResourceId)
                .into(holder.pic);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class viewholder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        ImageView pic;

        LinearLayout container;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.catNameTxt);
            pic = itemView.findViewById(R.id.imgCat);
            container = itemView.findViewById(R.id.category_container);

        }
    }
}
