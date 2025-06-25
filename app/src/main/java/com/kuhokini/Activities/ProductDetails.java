package com.kuhokini.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
//import com.hishd.tinycart.model.Cart;
//import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.APIModels.BannerResponse;
import com.kuhokini.APIModels.MainResponse;
import com.kuhokini.APIModels.ProductData;
import com.kuhokini.APIModels.ProductResponse;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.Adapters.FeaturedAdapter;
import com.kuhokini.Adapters.PhotosAdapter;
import com.kuhokini.Adapters.ProductsAdapter;
import com.kuhokini.Adapters.VariantAdapter;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.MainActivity;
import com.kuhokini.Models.BannerModel;
import com.kuhokini.R;
import com.kuhokini.TinyCart.TinyCart;
import com.kuhokini.databinding.ActivityProductDetailsBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.ArrayList;

import io.noties.markwon.Markwon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetails extends AppCompatActivity implements VariantAdapter.OnVariantClickListener {

    ActivityProductDetailsBinding binding;
    ApiService apiService;
    String productId;
    Activity activity;
    VariantResponse.Variant variantDetails;
    ArrayList<SlideModel> images = new ArrayList<>();
    int nextPageToken, currentPage = 0;
    private boolean isInitialLoad = true;
    private boolean isLoading = false;
    TinyCart cart;
    ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());
        activity = ProductDetails.this;
        cart = TinyCart.getInstance();
        apiService = RetrofitClient.getClient().create(ApiService.class);
        productId = Helper.getData(ProductDetails.this, "product_id");
        images.add(new SlideModel(R.drawable.placeholder, ScaleTypes.CENTER_CROP));

        LinearLayoutManager lnm = new LinearLayoutManager(ProductDetails.this);
        lnm.setOrientation(RecyclerView.HORIZONTAL);
        binding.variantRec.setLayoutManager(lnm);

        Call<ProductResponse> call = apiService.getProductDetails(productId);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    ProductResponse productResponse = response.body();
                    if (productResponse.getStatus().equalsIgnoreCase("success")){

                        ProductData details = productResponse.getData();
                        binding.availVariant.setText("Available Variant : " + details.getVariants().size());
                        VariantAdapter adapter = new VariantAdapter(ProductDetails.this, details.getVariants(),
                                ProductDetails.this);
                        binding.variantRec.setAdapter(adapter);

                        variantDetails = details.getVariants().get(0);
                        setVariantInfo(variantDetails);

                        if (details.getRating_info().getAverage_rating() == 1){
                            binding.star1.setVisibility(View.VISIBLE);
                        } else if (details.getRating_info().getAverage_rating() == 2) {
                            binding.star1.setVisibility(View.VISIBLE);
                            binding.star2.setVisibility(View.VISIBLE);
                        }else if (details.getRating_info().getAverage_rating() == 3) {
                            binding.star1.setVisibility(View.VISIBLE);
                            binding.star2.setVisibility(View.VISIBLE);
                            binding.star3.setVisibility(View.VISIBLE);
                        }else if (details.getRating_info().getAverage_rating() == 4) {
                            binding.star1.setVisibility(View.VISIBLE);
                            binding.star2.setVisibility(View.VISIBLE);
                            binding.star3.setVisibility(View.VISIBLE);
                            binding.star4.setVisibility(View.VISIBLE);
                        }else if (details.getRating_info().getAverage_rating() == 5
                                || details.getRating_info().getAverage_rating() == 0) {
                            binding.star1.setVisibility(View.VISIBLE);
                            binding.star2.setVisibility(View.VISIBLE);
                            binding.star3.setVisibility(View.VISIBLE);
                            binding.star4.setVisibility(View.VISIBLE);
                            binding.star5.setVisibility(View.VISIBLE);
                        }

                        binding.name.setText(details.getProduct_name());
                        binding.rating.setText(details.getRating_info().getAverage_rating() + " ("
                                + details.getRating_info().getReview_count() +" Reviews)");
                        binding.avgRating.setText(details.getRating_info().getAverage_rating() + "/5");
                        binding.totalRating.setText(details.getRating_info().getReview_count() + " Reviews");

                        binding.Star1.setText(String.valueOf(details.getRating_info().getRating_distribution().getOne_star()));
                        binding.Star2.setText(String.valueOf(details.getRating_info().getRating_distribution().getTwo_star()));
                        binding.Star3.setText(String.valueOf(details.getRating_info().getRating_distribution().getThree_star()));
                        binding.Star4.setText(String.valueOf(details.getRating_info().getRating_distribution().getFour_star()));
                        binding.Star5.setText(String.valueOf(details.getRating_info().getRating_distribution().getFive_star()));


                        binding.seekBar1.setProgress(details.getRating_info().getRating_distribution().getOne_star());
                        binding.seekBar2.setProgress(details.getRating_info().getRating_distribution().getTwo_star());
                        binding.seekBar3.setProgress(details.getRating_info().getRating_distribution().getThree_star());
                        binding.seekBar4.setProgress(details.getRating_info().getRating_distribution().getFour_star());
                        binding.seekBar5.setProgress(details.getRating_info().getRating_distribution().getFive_star());
                        

//                        Markwon markwon = Markwon.create(ProductDetails.this);
//                        markwon.setMarkdown(binding.description, details.getDescription());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.description.setText(
                                    Html.fromHtml(details.getDescription(), Html.FROM_HTML_MODE_COMPACT)
                            );
                        } else {
                            binding.description.setText(
                                    Html.fromHtml(details.getDescription())
                            );
                        }



                    }else {

                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {

            }
        });


//        getLifecycle().addObserver(binding.youtubePostView);
//        binding.youtubePostView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                youTubePlayer.loadVideo(videoModel.getYouTubeId(), 0f);
//            }
//        });

        setupRecyclerView();
        if (isInitialLoad) {
            getPostData();
            isInitialLoad = false; // Mark initial load as complete
        }
        imageSliders();


        binding.cartBtn.setOnClickListener(v->{
            //cart.addItem(variantDetails, details.getProduct_name(), variantDetails.getSelling_price(), 200);
            binding.cartText.setText("Added");
        });

        binding.buyBtn.setOnClickListener(v->{
            Intent i = new Intent(ProductDetails.this, CheckOut.class);
            startActivity(i);
        });


    }



    private void setVariantInfo(VariantResponse.Variant variantDetails) {
        if (variantDetails.getImages() != null && !variantDetails.getImages().isEmpty()){
            binding.imageView.setImageList(variantDetails.getImages(), ScaleTypes.CENTER_CROP);
        }else {
            binding.imageView.setImageList(images, ScaleTypes.CENTER_CROP);
        }

        binding.variantName.setText(variantDetails.getVarient_name());
        binding.normalPrice.setPaintFlags(binding.normalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Helper.startCounter(variantDetails.getNormal_price(), binding.normalPrice, "₹", "");
        Helper.startCounter(variantDetails.getSelling_price(), binding.sellingPrice, "₹", "");

        int saveAmount = variantDetails.getNormal_price() - variantDetails.getSelling_price();
        int discount = (int)(
                (saveAmount * 100) /variantDetails.getNormal_price()
        );
        binding.discountPrice.setText("Save ₹" + saveAmount);
        Helper.startCounter(discount, binding.offer, "", "% off");

        LinearLayoutManager lnm = new LinearLayoutManager(ProductDetails.this);
        lnm.setOrientation(RecyclerView.HORIZONTAL);
        binding.reviewImages.setLayoutManager(lnm);
        PhotosAdapter adapter = new PhotosAdapter(ProductDetails.this, variantDetails.getImages());
        binding.reviewImages.setAdapter(adapter);




    }

    @Override
    public void onChange(VariantResponse.Variant variant) {
        setVariantInfo(variant);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(ProductDetails.this, 2);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setItemViewCacheSize(50);
        binding.recyclerview.setDrawingCacheEnabled(true);
        binding.recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && nextPageToken != 0) {
                    isLoading = true;
                    getPostData();
                }
            }
        });
    }

    void getPostData() {
//        binding.loadMore.setVisibility(View.VISIBLE);
//        binding.noData.setVisibility(View.GONE); // Hide noData initially

        if (nextPageToken == 0 && adapter != null) {
            adapter.clearItems(); // clear old data on new search
            binding.similarRecyclerView.showShimmerAdapter();
            binding.recyclerview.showShimmerAdapter();
        }

        Call<MainResponse> call = apiService.fetchProducts(nextPageToken, null);
        call.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MainResponse apiResponse = response.body();
                    if (apiResponse.getStatus().equalsIgnoreCase("success")) {
                        if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                            if (adapter == null) {
                                adapter = new ProductsAdapter(ProductDetails.this, apiResponse.getData(),
                                        new ProductsAdapter.OnCartChangedListener() {
                                            @Override
                                            public void onCartChanged() {
                                                //updateCartBadge();
                                            }
                                        });
                                binding.recyclerview.setAdapter(adapter);
                                binding.similarRecyclerView.hideShimmerAdapter();
                                binding.recyclerview.hideShimmerAdapter();

                                LinearLayoutManager lnm = new LinearLayoutManager(ProductDetails.this);
                                lnm.setOrientation(RecyclerView.HORIZONTAL);
                                binding.similarRecyclerView.setLayoutManager(lnm);
                                FeaturedAdapter adapter1 = new FeaturedAdapter(ProductDetails.this, apiResponse.getData());
                                binding.similarRecyclerView.setAdapter(adapter1);
                            } else {
                                if (nextPageToken == 0) {
                                    adapter.setItems(apiResponse.getData()); // replace data
                                } else {
                                    adapter.addItems(apiResponse.getData()); // append data
                                }
                            }

                            if (apiResponse.getNextPageToken() != null) {
                                nextPageToken = Integer.parseInt(apiResponse.getNextPageToken());
                                currentPage = (nextPageToken / 15);
                            }

                            if (apiResponse.getData().size() < 15) {
                                nextPageToken = 0; // No more data to load
                            }
                        }
                    }
                }
                //binding.loadMore.setVisibility(View.GONE);
                isLoading = false;
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
//                binding.noData.setVisibility(View.VISIBLE);
//                binding.loadMore.setVisibility(View.GONE);
                binding.similarRecyclerView.hideShimmerAdapter();
                binding.recyclerview.hideShimmerAdapter();
                isLoading = false;
            }
        });
    }

    void imageSliders(){
        Call<BannerResponse> call = apiService.getBanners();
        call.enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                if (response.isSuccessful() && response != null){
                    BannerResponse categoryResponse = response.body();
                    if (categoryResponse.getStatus().equalsIgnoreCase("success")){
                        binding.viewFlipper.removeAllViews();
                        for (BannerModel bannerModel : categoryResponse.getData()) {
                            ImageView imageView = new ImageView(ProductDetails.this);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            if (!activity.isDestroyed()){
                                Glide.with(ProductDetails.this)
                                        .load(bannerModel.getImageUrl())
                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                        .placeholder(R.drawable.placeholder) // Optional placeholder
                                        .into(imageView);
                            }

                            imageView.setOnClickListener(view -> {
                                int currentIndex = binding.viewFlipper.getDisplayedChild(); // Get current displayed child index
                                BannerModel activeBanner = categoryResponse.getData().get(currentIndex);
                                if (activeBanner.getMessage() == null && activeBanner.getLink() == null){
                                    Toast.makeText(ProductDetails.this, "No link attached!", Toast.LENGTH_SHORT).show();
                                }else {
                                    showBannerDialog(activeBanner.getMessage(), activeBanner.getLink(), activeBanner.getBtnTxt());                                        }
                            });
                            binding.viewFlipper.addView(imageView);
                        }

                        binding.viewFlipper.setFlipInterval(5000);
                        binding.viewFlipper.startFlipping();

                        binding.cardView.setVisibility(View.VISIBLE);
                    }else {
                        binding.cardView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                binding.cardView.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void showBannerDialog(String message, String link, String btnTxt) {

        String btn = "Open Link";
        if (btnTxt != null && !btnTxt.isEmpty()){
            btn = btnTxt;
        }

        if (message != null && !message.isEmpty() && (link == null || link.isEmpty())){
            Helper.showActionDialog(ProductDetails.this, "Message Found", message,
                    "Okay", null, true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {

                        }

                        @Override
                        public void onNoButtonClicked() {

                        }

                        @Override
                        public void onCloseButtonClicked() {

                        }
                    });
        }else if (message != null && !message.isEmpty() && (link != null || !link.isEmpty())){
            Helper.showActionDialog(ProductDetails.this, "Message Found", message,
                    btn, null, true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {
                            Helper.openLink(ProductDetails.this, link);
                        }

                        @Override
                        public void onNoButtonClicked() {

                        }

                        @Override
                        public void onCloseButtonClicked() {

                        }
                    });
        }else {
            Helper.showActionDialog(ProductDetails.this, "Link Found", "For opening attach link, click below button. " +
                            "It will redirect you to the linked page!",
                    btn, null, true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {
                            Helper.openLink(ProductDetails.this, link);
                        }

                        @Override
                        public void onNoButtonClicked() {

                        }

                        @Override
                        public void onCloseButtonClicked() {

                        }
                    });
        }
    }
}