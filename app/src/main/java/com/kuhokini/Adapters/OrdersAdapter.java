package com.kuhokini.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kuhokini.Activities.ReceiptActivity;
import com.kuhokini.Models.OrderModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildOrdersBinding;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>{

    Activity activity;
    List<OrderModel> models;

    public OrdersAdapter(Activity activity, List<OrderModel> models) {
        this.activity = activity;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel orderModel = models.get(position);

        holder.binding.cusName.setText(orderModel.getName());
        holder.binding.proName.setText(orderModel.getProductName());
        holder.binding.amount.setText("₹"+orderModel.getAmount()+"/-");


        holder.binding.status.setText(orderModel.getStatus());
        if (orderModel.getStatus().equalsIgnoreCase("success")){
            holder.binding.status.setTextColor(activity.getColor(R.color.greenDark));
            holder.binding.status.setBackground(activity.getDrawable(R.drawable.bg_green_green));
        } else if (orderModel.getStatus().equalsIgnoreCase("failed")) {
            holder.binding.status.setTextColor(activity.getColor(R.color.red));
            holder.binding.status.setBackground(activity.getDrawable(R.drawable.bg_red_red));
        } else {
            holder.binding.status.setTextColor(activity.getColor(R.color.orange));
            holder.binding.status.setBackground(activity.getDrawable(R.drawable.bg_orange_orange));
        }

        if (orderModel.getImg() != null && !activity.isDestroyed()){
            Glide.with(holder.itemView)
                    .load(orderModel.getImg())
                    .placeholder(activity.getDrawable(R.drawable.placeholder))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.binding.img);
        }


        holder.itemView.setOnClickListener(v->{
            activity.startActivity(new Intent(activity.getApplicationContext(), ReceiptActivity.class));
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildOrdersBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildOrdersBinding.bind(itemView);
        }
    }
}