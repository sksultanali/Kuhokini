package com.kuhokini.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.gson.Gson;
import com.kuhokini.APIModels.ProductData;
import com.kuhokini.APIModels.ProductResponse;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.Activities.ProductDetails;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.DB_Helper_WishList;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.Models.WishListModel;
import com.kuhokini.R;
import com.kuhokini.TinyCart.TinyCart;
import com.kuhokini.databinding.ChildProductBinding;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishLIstAdapter extends RecyclerView.Adapter<WishLIstAdapter.ViewHolder>{

    Activity activity;
    ArrayList<WishListModel> models;
    LinearLayout linearLayout;
    ApiService apiService;
    Animation animation;
    Gson gson;
    TinyCart cart;
    DB_Helper_WishList db_wishList_helper;
    ArrayList<SlideModel> images = new ArrayList<>();

    public WishLIstAdapter(Activity activity, ArrayList<WishListModel> models,
                           LinearLayout linearLayout) {
        this.activity = activity;
        this.models = models;
        this.linearLayout = linearLayout;
        images.add(new SlideModel(R.drawable.placeholder, ScaleTypes.CENTER_CROP));
        gson = new Gson();
        cart = TinyCart.getInstance();
        db_wishList_helper = new DB_Helper_WishList(activity);
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WishListModel wishListModel = models.get(position);
        animation = AnimationUtils.loadAnimation(activity, R.anim.item_slide_down);
        holder.itemView.startAnimation(animation);

        Call<ProductResponse> call = apiService.getProductDetails(wishListModel.getId());
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    ProductResponse productResponse = response.body();
                    if (productResponse.getStatus().equalsIgnoreCase("success")){
                        holder.binding.noImage.setVisibility(View.GONE);
                        holder.binding.consLayout.setVisibility(View.VISIBLE);

                        ProductData details = productResponse.getData();
                        VariantResponse.Variant variantDetails = details.getVariants().get(0);

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
                        holder.binding.rating.setText(details.getRating_info().getReview_count() + " Reviews");
                        holder.binding.normalPrice.setPaintFlags(holder.binding.normalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.binding.normalPrice.setText("₹"+variantDetails.getNormal_price());
                        holder.binding.sellingPrice.setText("₹"+variantDetails.getSelling_price());

                        holder.binding.addCart.setOnClickListener(v -> {
                            cart.addItem(variantDetails, details.getProduct_name(), variantDetails.getSelling_price(), details.getWeight());
                            holder.binding.addCart.setEnabled(false);
                            new CountDownTimer(3000, 1000) {
                                int count = 3;
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    holder.binding.addCart.setText("Added (" + count + ")");
                                    count--;
                                }
                                @Override
                                public void onFinish() {
                                    holder.binding.addCart.setEnabled(true);
                                    holder.binding.addCart.setText("Add Again");
                                }
                            }.start();
                        });

                        int discount = (int)(
                                ((variantDetails.getNormal_price() - variantDetails.getSelling_price())*100)
                                        /variantDetails.getNormal_price()
                        );
                        holder.binding.offer.setText(discount + "% Off");

                    }else {
                        holder.binding.noImage.setVisibility(View.VISIBLE);
                        holder.binding.consLayout.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                holder.binding.noImage.setVisibility(View.VISIBLE);
                holder.binding.consLayout.setVisibility(View.INVISIBLE);
            }
        });

        holder.binding.wishListBtn.setLiked(true);
        holder.binding.wishListBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                db_wishList_helper.deleteSearchQuery(wishListModel.getName());
                models.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();

                if (models.isEmpty()){
                    linearLayout.setVisibility(View.VISIBLE);
                }

            }
        });
        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(activity.getApplicationContext(), ProductDetails.class);
            intent.putExtra("id", wishListModel.getId());
            activity.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildProductBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildProductBinding.bind(itemView);
        }
    }
}
