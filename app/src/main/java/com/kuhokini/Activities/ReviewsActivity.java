package com.kuhokini.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuhokini.APIModels.MainResponse;
import com.kuhokini.APIModels.ReviewResponse;
import com.kuhokini.Adapters.ProductsAdapter;
import com.kuhokini.Adapters.ReviewAdapter;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityReviewsBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity {

    ActivityReviewsBinding binding;
    ApiService apiService;
    int nextPageToken, currentPage = 0, productPrice;
    private boolean isInitialLoad = true;
    ReviewAdapter adapter;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityReviewsBinding.inflate(getLayoutInflater());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());

        setupRecyclerView();
        if (isInitialLoad) {
            getPostData();
            isInitialLoad = false; // Mark initial load as complete
        }




    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(ReviewsActivity.this);
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
//        binding.noData.setVisibility(View.GONE); // Hide noData initially

        if (nextPageToken == 0 && adapter != null) {
            adapter.clearItems();
            binding.recyclerview.showShimmerAdapter();
        }

        Call<ReviewResponse> call = apiService.fetchReviews(nextPageToken, null, null, "");
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse apiResponse = response.body();
                    if (apiResponse.getStatus().equalsIgnoreCase("success")) {
                        if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                            if (adapter == null) {
                                adapter = new ReviewAdapter(ReviewsActivity.this, apiResponse.getData());
                                binding.recyclerview.setAdapter(adapter);
                                binding.recyclerview.hideShimmerAdapter();
                            } else {
                                if (nextPageToken == 0) {
                                    adapter.setItems(apiResponse.getData()); // replace data
                                } else {
                                    adapter.addItems(apiResponse.getData()); // append data
                                }
                            }

                            if (apiResponse.getNextPageToken() != 0) {
                                nextPageToken = apiResponse.getNextPageToken();
                                currentPage = (nextPageToken / 15);
                            }

                            if (apiResponse.getData().size() < 15) {
                                nextPageToken = 0; // No more data to load
                            }
                        }
                    }
                }else {
                    binding.noData.setVisibility(View.VISIBLE);
                }
                binding.loadMore.setVisibility(View.GONE);
                isLoading = false;
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                //binding.noData.setVisibility(View.VISIBLE);
                binding.loadMore.setVisibility(View.GONE);
                binding.recyclerview.hideShimmerAdapter();
                isLoading = false;
            }
        });
    }
}