package com.kuhokini.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.APIModels.ProductData;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.Activities.ProductDetails;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.DB_Helper_WishList;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.Models.WishListModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildProductBinding;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder>{

    Activity activity;
    List<ProductData> models;
    ArrayList<SlideModel> images = new ArrayList<>();
    ApiService apiService;
    ProgressDialog progressDialog;
    String tableName;
    DB_Helper_WishList wishList;
    Cart cart;
    OnCartChangedListener cartChangedListener;

    public interface OnCartChangedListener {
        void onCartChanged();
    }


    public ProductsAdapter(Activity activity, List<ProductData> models, OnCartChangedListener cartChangedListener) {
        this.activity = activity;
        this.models = models;
        this.cartChangedListener = cartChangedListener;
        images.add(new SlideModel(R.drawable.placeholder, ScaleTypes.CENTER_CROP));
        wishList = new DB_Helper_WishList(activity);
        apiService = RetrofitClient.getClient().create(ApiService.class);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Connecting Server...");
        tableName = "products";
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductData details = models.get(position);
        VariantResponse.Variant variantDetails = details.getVariants().get(0);

        holder.binding.wishListBtn.setLiked(wishList.isHotelInWishlist(
                String.valueOf(details.getProduct_id())
        ));

        if (variantDetails.getImages() != null && !variantDetails.getImages().isEmpty()){
            holder.binding.imageView.setImageList(variantDetails.getImages(), ScaleTypes.CENTER_CROP);
        }else {
            holder.binding.imageView.setImageList(images, ScaleTypes.CENTER_CROP);
        }

        if (details.getRating_info().getAverage_rating() == 1){
            holder.binding.star1.setVisibility(View.VISIBLE);
        } else if (details.getRating_info().getAverage_rating() == 2) {
            holder.binding.star1.setVisibility(View.VISIBLE);
            holder.binding.star2.setVisibility(View.VISIBLE);
        }else if (details.getRating_info().getAverage_rating() == 3) {
            holder.binding.star1.setVisibility(View.VISIBLE);
            holder.binding.star2.setVisibility(View.VISIBLE);
            holder.binding.star3.setVisibility(View.VISIBLE);
        }else if (details.getRating_info().getAverage_rating() == 4) {
            holder.binding.star1.setVisibility(View.VISIBLE);
            holder.binding.star2.setVisibility(View.VISIBLE);
            holder.binding.star3.setVisibility(View.VISIBLE);
            holder.binding.star4.setVisibility(View.VISIBLE);
        }else if (details.getRating_info().getAverage_rating() == 5
                || details.getRating_info().getAverage_rating() == 0) {
            holder.binding.star1.setVisibility(View.VISIBLE);
            holder.binding.star2.setVisibility(View.VISIBLE);
            holder.binding.star3.setVisibility(View.VISIBLE);
            holder.binding.star4.setVisibility(View.VISIBLE);
            holder.binding.star5.setVisibility(View.VISIBLE);
        }

        holder.binding.name.setText(details.getProduct_name());
        holder.binding.rating.setText(details.getRating_info().getAverage_rating() + " (" + details.getRating_info().getReview_count() + ")");
        holder.binding.normalPrice.setPaintFlags(holder.binding.normalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.normalPrice.setText("₹"+variantDetails.getNormal_price());
        holder.binding.sellingPrice.setText("₹"+variantDetails.getSelling_price());

        int discount = (int)(
                ((variantDetails.getNormal_price() - variantDetails.getSelling_price())*100)
                        /variantDetails.getNormal_price()
        );
        holder.binding.offer.setText(discount + "% Off");

        holder.itemView.setOnClickListener(v->{
            Helper.saveData(activity, "product_id", String.valueOf(details.getProduct_id()));
            activity.startActivity(new Intent(activity.getApplicationContext(), ProductDetails.class));
        });

        holder.binding.wishListBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                WishListModel wishListModel = new WishListModel();
                wishListModel.setId(String.valueOf(details.getProduct_id()));
                wishListModel.setName(details.getProduct_name());
                wishList.addWishList(wishListModel);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                wishList.deleteSearchQuery(details.getProduct_name());
            }
        });



        holder.binding.addCart.setOnClickListener(v->{
            cart.addItem(variantDetails,1);
            if (cartChangedListener != null) {
                cartChangedListener.onCartChanged();
            }
            holder.binding.addCart.setEnabled(false);
            holder.binding.addCart.setText("Added");

        });


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void addItems(List<ProductData> newItems) {
        int startPosition = models.size();
        models.addAll(newItems);
        notifyItemRangeInserted(startPosition, newItems.size());
    }

    public void clearItems() {
        models.clear();
        notifyDataSetChanged();
    }

    public void setItems(List<ProductData> newItems) {
        models.clear();
        models.addAll(newItems);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildProductBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildProductBinding.bind(itemView);
        }
    }
}
