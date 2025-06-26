package com.kuhokini.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Models.ReviewsModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ItemReviewBinding;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    Activity activity;
    List<ReviewsModel> models;

    public ReviewAdapter(Activity activity, List<ReviewsModel> models) {
        this.activity = activity;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewsModel reviewsModel = models.get(position);
        
        holder.binding.profileName.setText(reviewsModel.getUser_name());
        if (reviewsModel.getRating() == 1){
            holder.binding.star1.setVisibility(View.VISIBLE);
        } else if (reviewsModel.getRating() == 2) {
            holder.binding.star1.setVisibility(View.VISIBLE);
            holder.binding.star2.setVisibility(View.VISIBLE);
        }else if (reviewsModel.getRating() == 3) {
            holder.binding.star1.setVisibility(View.VISIBLE);
            holder.binding.star2.setVisibility(View.VISIBLE);
            holder.binding.star3.setVisibility(View.VISIBLE);
        }else if (reviewsModel.getRating() == 4) {
            holder.binding.star1.setVisibility(View.VISIBLE);
            holder.binding.star2.setVisibility(View.VISIBLE);
            holder.binding.star3.setVisibility(View.VISIBLE);
            holder.binding.star4.setVisibility(View.VISIBLE);
        }else if (reviewsModel.getRating() == 5
                || reviewsModel.getRating() == 0) {
            holder.binding.star1.setVisibility(View.VISIBLE);
            holder.binding.star2.setVisibility(View.VISIBLE);
            holder.binding.star3.setVisibility(View.VISIBLE);
            holder.binding.star4.setVisibility(View.VISIBLE);
            holder.binding.star5.setVisibility(View.VISIBLE);
        }

        if (reviewsModel.getImg() != null && !activity.isDestroyed()){
            Glide.with(activity)
                    .load(reviewsModel.getImg())
                    .placeholder(activity.getDrawable(R.drawable.placeholder))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.binding.image);
            holder.binding.image.setVisibility(View.VISIBLE);
        }else {
            holder.binding.image.setVisibility(View.GONE);
        }

        if (reviewsModel.getComment() != null){
            holder.binding.caption.setText(reviewsModel.getComment());
        }else if (reviewsModel.getImg() != null){
            holder.binding.caption.setText("This user shared a image and also rated "+ reviewsModel.getRating() +" to this product.");
        }else {
            holder.binding.caption.setText("This user only rated "+ reviewsModel.getRating() +" to this product. No other review found");
        }

        holder.binding.timeAgo.setText(Helper.formatDate(reviewsModel.getCreated_at(), "yyyy-MM-dd HH:mm:ss", "dd LLL yyyy"));

        
        
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemReviewBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReviewBinding.bind(itemView);
        }
    }
}
