package com.kuhokini.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.denzcoskun.imageslider.models.SlideModel;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildVariantBinding;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder>{

    Activity activity;
    List<SlideModel> models;
    Animation animation;

    public PhotosAdapter(Activity activity, List<SlideModel> models) {
        this.activity = activity;
        this.models = models;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_variant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SlideModel variant = models.get(position);
        animation = AnimationUtils.loadAnimation(activity, R.anim.slide_from_left_pre);
        holder.itemView.startAnimation(animation);

        if (variant.getImageUrl() != null && !variant.getImageUrl().isEmpty() && !activity.isDestroyed()){
            Glide.with(activity)
                    .load(variant.getImageUrl())
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
        ChildVariantBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildVariantBinding.bind(itemView);
        }
    }
}
