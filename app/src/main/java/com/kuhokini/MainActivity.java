package com.kuhokini;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.hishd.tinycart.model.Cart;
//import com.hishd.tinycart.model.Item;
//import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.APIModels.BannerResponse;
import com.kuhokini.APIModels.CategoryResponse;
import com.kuhokini.APIModels.MainResponse;
import com.kuhokini.Activities.CartActivity;
import com.kuhokini.Activities.MenuActivity;
import com.kuhokini.Activities.NotificationActivity;
import com.kuhokini.Activities.SearchActivity;
import com.kuhokini.Activities.WishListActivity;
import com.kuhokini.Adapters.CategoryAdapter;
import com.kuhokini.Adapters.FeaturedAdapter;
import com.kuhokini.Adapters.ProductsAdapter;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.Helpers.TypeAnimation;
import com.kuhokini.Helpers.UpdateManager;
import com.kuhokini.Models.BannerModel;
import com.kuhokini.Models.CategoryModel;
import com.kuhokini.TinyCart.TinyCart;
import com.kuhokini.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ApiService apiService;
    MainActivity activity;
    private UpdateManager updateManager;
    private List<String> textList = Arrays.asList(
            "Discover timeless rings...",
            "Find your perfect saree...",
            "Shop elegant golden bangles...",
            "Explore trending bridal sets...",
            "Unveil grace with pearl necklaces...",
            "Adorn yourself with tradition..."
    );

    private Handler handler = new Handler(Looper.getMainLooper());
    int nextPageToken, currentPage = 0;
    private boolean isInitialLoad = true;
    private boolean isLoading = false;
    ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        activity = MainActivity.this;
        apiService = RetrofitClient.getClient().create(ApiService.class);

        TypeAnimation typeAnimation = new TypeAnimation(handler, textList, binding.searchTags);
        typeAnimation.startTypingAnimation();

        updateManager = new UpdateManager(this);
        updateManager.checkForUpdates();

        loadCategory();
        imageSliders();
        setupRecyclerView();
        if (isInitialLoad) {
            getPostData();
            isInitialLoad = false; // Mark initial load as complete
        }

        binding.menuBtn.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    MainActivity.this,
                    R.anim.s2l, // Entry animation
                    R.anim.s2r // Exit animation
            );
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent, options.toBundle());
        });

        binding.searchTags.setOnClickListener(v->binding.laySearch.performClick());
        binding.laySearch.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        binding.wishList.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, WishListActivity.class));
        });

        binding.cart.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });

        binding.notification.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
        });





    }

    private void updateCartBadge() {
        TinyCart cart = TinyCart.getInstance();
        int totalItems = cart.getTotalQuantity(); // Using the built-in total quantity from our TinyCart

        if (totalItems < 1) {
            binding.cartBadge.setVisibility(View.GONE);
        } else {
            binding.cartBadge.setVisibility(View.VISIBLE);
            binding.cartBadge.setText(String.valueOf(totalItems));
        }
    }


    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
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
            binding.featuredRec.showShimmerAdapter();
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
                                adapter = new ProductsAdapter(MainActivity.this, apiResponse.getData(),
                                        new ProductsAdapter.OnCartChangedListener() {
                                            @Override
                                            public void onCartChanged() {
                                                updateCartBadge();
                                            }
                                        });
                                binding.recyclerview.setAdapter(adapter);
                                binding.featuredRec.hideShimmerAdapter();
                                binding.recyclerview.hideShimmerAdapter();

                                LinearLayoutManager lnm = new LinearLayoutManager(MainActivity.this);
                                lnm.setOrientation(RecyclerView.HORIZONTAL);
                                binding.featuredRec.setLayoutManager(lnm);
                                FeaturedAdapter adapter1 = new FeaturedAdapter(MainActivity.this, apiResponse.getData());
                                binding.featuredRec.setAdapter(adapter1);
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
                binding.featuredRec.hideShimmerAdapter();
                binding.recyclerview.hideShimmerAdapter();
                isLoading = false;
            }
        });
    }

    void loadCategory(){
        Call<CategoryResponse> call = apiService.fetchCategories();

        LinearLayoutManager lnm = new LinearLayoutManager(this);
        lnm.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.categoryRec.setLayoutManager(lnm);
        binding.categoryRec.showShimmerAdapter();

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response != null){
                    CategoryResponse categoryResponse = response.body();
                    if (categoryResponse.getStatus().equalsIgnoreCase("success")){
                        CategoryAdapter adapter = new CategoryAdapter(MainActivity.this,
                                categoryResponse.getData(), "category_tbl", new CategoryAdapter.OnChangeListener() {
                            @Override
                            public void onSelect(CategoryModel categoryModel) {

                            }
                        });
                        binding.categoryRec.setAdapter(adapter);
                        binding.categoryRec.hideShimmerAdapter();
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                binding.categoryRec.hideShimmerAdapter();
            }
        });
    }

    void showError(String error){
        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
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
                            ImageView imageView = new ImageView(MainActivity.this);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            if (!activity.isDestroyed()){
                                Glide.with(MainActivity.this)
                                        .load(bannerModel.getImageUrl())
                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                        .placeholder(R.drawable.placeholder) // Optional placeholder
                                        .into(imageView);
                            }

                            imageView.setOnClickListener(view -> {
                                int currentIndex = binding.viewFlipper.getDisplayedChild(); // Get current displayed child index
                                BannerModel activeBanner = categoryResponse.getData().get(currentIndex);
                                if (activeBanner.getMessage() == null && activeBanner.getLink() == null){
                                    Toast.makeText(MainActivity.this, "No link attached!", Toast.LENGTH_SHORT).show();
                                }else {
                                    showBannerDialog(activeBanner.getMessage(), activeBanner.getLink(), activeBanner.getBtnTxt());                                        }
                            });
                            binding.viewFlipper.addView(imageView);
                        }

                        binding.viewFlipper.setFlipInterval(5000);
                        binding.viewFlipper.startFlipping();

                        binding.bannerLayout.setVisibility(View.VISIBLE);
                    }else {
                        binding.bannerLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                binding.bannerLayout.setVisibility(View.GONE);
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
            Helper.showActionDialog(MainActivity.this, "Message Found", message,
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
            Helper.showActionDialog(MainActivity.this, "Message Found", message,
                    btn, null, true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {
                            Helper.openLink(MainActivity.this, link);
                        }

                        @Override
                        public void onNoButtonClicked() {

                        }

                        @Override
                        public void onCloseButtonClicked() {

                        }
                    });
        }else {
            Helper.showActionDialog(MainActivity.this, "Link Found", "For opening attach link, click below button. " +
                            "It will redirect you to the linked page!",
                    btn, null, true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {
                            Helper.openLink(MainActivity.this, link);
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

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
        updateManager.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}