package com.example.foodapp.Adapter;

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
import com.example.foodapp.Domain.Notification;
import com.example.foodapp.Listener.NotificationListener;
import com.example.foodapp.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewholder> {

    ArrayList<Notification> items;
    Context context;

    NotificationListener listener;

    public NotificationAdapter(ArrayList<Notification> items, NotificationListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_notification, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.msgTxt.setText(items.get(position).getMessage());
        holder.dateTxt.setText(items.get(position).getDate());

        if (listener != null){
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(items.get(holder.getAdapterPosition()));
                }
            });
        }

        Glide.with(context)
                .load(items.get(position).getImg())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        TextView titleTxt, msgTxt, dateTxt;
        ConstraintLayout container;

        ImageView img;
        public viewholder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            msgTxt = itemView.findViewById(R.id.msgTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            container = itemView.findViewById(R.id.notiContainer);
            img = itemView.findViewById(R.id.img);
        }
    }
}
