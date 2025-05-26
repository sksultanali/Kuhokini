package com.kuhokini.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.Models.CouponModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildCouponBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder>{

    Activity activity;
    List<CouponModel> models;
    ApiService apiService;

    public CouponAdapter(Activity activity, List<CouponModel> models) {
        this.activity = activity;
        this.models = models;
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CouponModel couponModel = models.get(position);

        holder.binding.expireDate.setText("Expire: " + Helper.formatDate(
               couponModel.getValid(), "yyyyMMdd", "dd LLL, yy"
        ));

        holder.binding.code.setText(couponModel.getCode());
        holder.binding.type.setText(couponModel.getType());
        if (couponModel.getType().equalsIgnoreCase("Flat Discount")) {
            holder.binding.amount.setText("₹"+couponModel.getPercentageOrAmount()+" Off");
        }else {
            holder.binding.amount.setText(couponModel.getPercentageOrAmount()+"% Off");
        }

        holder.binding.delete.setOnClickListener(v->{

        });



    }

    @Override
    public int getItemCount() {
        return models.size();
    }

   public class ViewHolder extends RecyclerView.ViewHolder{
       ChildCouponBinding binding;
        public ViewHolder(@NonNull View itemView) {
           super(itemView);
           binding = ChildCouponBinding.bind(itemView);
       }
   }
}