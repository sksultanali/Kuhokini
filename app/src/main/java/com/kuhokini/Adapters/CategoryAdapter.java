package com.kuhokini.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.Models.CategoryModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    Activity activity;
    List<CategoryModel> models;
    ApiService apiService;
    ProgressDialog progressDialog;
    String tableName;
    Animation animation;

    public CategoryAdapter(Activity activity, List<CategoryModel> models, String tableName) {
        this.activity = activity;
        this.models = models;
        this.tableName = tableName;
        apiService = RetrofitClient.getClient().create(ApiService.class);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Connecting Server...");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CategoryModel categoryModel = models.get(position);
        animation = AnimationUtils.loadAnimation(activity, R.anim.slide_from_left_pre);
        holder.itemView.startAnimation(animation);

        holder.binding.name.setText(categoryModel.getName());
        String count = categoryModel.getSubcategory_count() != null ? categoryModel.getSubcategory_count()
                : categoryModel.getProduct_count();
        holder.binding.sub.setText("("+count+")");

        if (categoryModel.getImage() != null && !activity.isDestroyed()){
            Glide.with(activity)
                    .load(categoryModel.getImage())
                    .placeholder(activity.getDrawable(R.drawable.placeholder))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.binding.img);
        }

        holder.itemView.setOnClickListener(v->{
            if (tableName.equalsIgnoreCase("category_tbl")){
//                Intent i = new Intent(activity.getApplicationContext(), SubCatActivity.class);
//                i.putExtra("parent_id", categoryModel.getId());
//                i.putExtra("name", categoryModel.getName());
//                activity.startActivity(i);
            }else {

            }
        });



    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildCategoryBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildCategoryBinding.bind(itemView);
        }
    }
}
