package com.kuhokini.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildCategoryBinding;
import com.kuhokini.databinding.ChildVariantBinding;

import java.util.List;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.ViewHolder>{

    Activity activity;
    List<VariantResponse.Variant> models;
    OnVariantClickListener onVariantClickListener;
    Animation animation;

    public VariantAdapter(Activity activity, List<VariantResponse.Variant> models, OnVariantClickListener onVariantClickListener) {
        this.activity = activity;
        this.models = models;
        this.onVariantClickListener = onVariantClickListener;
    }

    public interface OnVariantClickListener{
        void onChange(VariantResponse.Variant variant);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_variant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VariantResponse.Variant variant = models.get(position);
        animation = AnimationUtils.loadAnimation(activity, R.anim.slide_from_left_pre);
        holder.itemView.startAnimation(animation);

        if (variant.getImages() != null && !variant.getImages().isEmpty() && !activity.isDestroyed()){
            Glide.with(activity)
                    .load(variant.getImages().get(0).getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(activity.getDrawable(R.drawable.placeholder))
                    .into(holder.binding.img);
        }

        if (variant.getStock() == 0){
            holder.binding.img.setAlpha(0.4F);
        }else {
            holder.binding.img.setAlpha(1.0F);
        }

        holder.itemView.setOnClickListener(v->{
            onVariantClickListener.onChange(variant);
        });

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
