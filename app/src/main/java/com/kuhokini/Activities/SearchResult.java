package com.kuhokini.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuhokini.APIModels.MainResponse;
import com.kuhokini.Adapters.ProductsAdapter;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.MainActivity;
import com.kuhokini.R;
import com.kuhokini.TinyCart.TinyCart;
import com.kuhokini.databinding.ActivitySearchResultBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResult extends AppCompatActivity implements ProductsAdapter.OnCartChangedListener{

    ActivitySearchResultBinding binding;
    ApiService apiService;
    String keyword;
    int nextPageToken, currentPage = 0;
    private boolean isInitialLoad = true;
    private boolean isLoading = false;
    ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiService = RetrofitClient.getClient().create(ApiService.class);
        binding.goBack.setOnClickListener(v->onBackPressed());
        binding.wishList.setOnClickListener(v->{startActivity(new Intent(SearchResult.this, WishListActivity.class));});
        binding.cart.setOnClickListener(v->{startActivity(new Intent(SearchResult.this, CartActivity.class));});
        keyword = getIntent().getStringExtra("keyword");
        binding.searchTags.setText(keyword);

        binding.searchTags.setOnClickListener(v->{
            Intent intent = new Intent(SearchResult.this, SearchActivity.class);
            intent.putExtra("keyword", keyword);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    SearchResult.this,
                    R.anim.s2l, // Entry animation
                    R.anim.s2r // Exit animation
            );
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent, options.toBundle());
            finish();
        });

        updateCartBadge();

        setupRecyclerView();
        if (isInitialLoad) {
            binding.progressBar.setVisibility(View.VISIBLE);
            getPostData();
            isInitialLoad = false; // Mark initial load as complete
        }


    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(SearchResult.this, 2);
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
        binding.loadMore.setVisibility(View.VISIBLE);
        binding.noData.setVisibility(View.GONE); // Hide noData initially

        if (nextPageToken == 0 && adapter != null) {
            adapter.clearItems(); // clear old data on new search
        }

        Call<MainResponse> call = apiService.fetchProducts(nextPageToken, keyword);
        call.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MainResponse apiResponse = response.body();
                    if (apiResponse.getStatus().equalsIgnoreCase("success")) {
                        if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                            if (adapter == null) {
                                adapter = new ProductsAdapter(SearchResult.this, apiResponse.getData(),
                                        SearchResult.this);
                                binding.recyclerview.setAdapter(adapter);
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
                        } else {
                            if (adapter == null || adapter.getItemCount() == 0) {
                                binding.noData.setVisibility(View.VISIBLE);
                            }
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                } else {
                    binding.noData.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                }

                binding.loadMore.setVisibility(View.GONE);
                isLoading = false;
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                binding.noData.setVisibility(View.VISIBLE);
                binding.loadMore.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                isLoading = false;
            }
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

    @Override
    public void onCartChanged() {
        updateCartBadge();
    }
}