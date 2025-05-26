package com.kuhokini.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.Models.BannerModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildBannersBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder>{

    Activity activity;
    List<BannerModel> models;
    ApiService apiService;
    ProgressDialog progressDialog;

    public BannerAdapter(Activity activity, List<BannerModel> models) {
        this.activity = activity;
        this.models = models;
        apiService = RetrofitClient.getClient().create(ApiService.class);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Connecting Server...");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_banners, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BannerModel bannerModel = models.get(position);
        holder.binding.serial.setText(bannerModel.getSerial());

        if (bannerModel.getImageUrl() != null && !activity.isDestroyed()){
            Glide.with(activity)
                    .load(bannerModel.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(activity.getDrawable(R.drawable.placeholder))
                    .into(holder.binding.img);
        }



    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildBannersBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildBannersBinding.bind(itemView);
        }
    }
}
