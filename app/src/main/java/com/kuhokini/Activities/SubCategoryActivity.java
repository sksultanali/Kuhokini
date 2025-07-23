package com.kuhokini.Activities;

import android.app.Activity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kuhokini.APIModels.CategoryResponse;
import com.kuhokini.APIModels.MainResponse;
import com.kuhokini.Adapters.CategoryAdapter;
import com.kuhokini.Adapters.FeaturedAdapter;
import com.kuhokini.Adapters.ProductsAdapter;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.MainActivity;
import com.kuhokini.Models.CategoryModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivitySubCategoryBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryActivity extends AppCompatActivity {

    ActivitySubCategoryBinding binding;
    ApiService apiService;
    Activity activity;
    String catId, catName, subCatId;
    int nextPageToken, currentPage = 0;
    private boolean isInitialLoad = true;
    private boolean isLoading = false;
    ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());

        catId = getIntent().getStringExtra("catId");
        catName = getIntent().getStringExtra("catName");
        binding.title.setText(catName);

        activity = SubCategoryActivity.this;
        apiService = RetrofitClient.getClient().create(ApiService.class);

        loadCategory();
        setupRecyclerView();
        if (isInitialLoad) {
            getPostData(catId, subCatId);
            isInitialLoad = false; // Mark initial load as complete
        }







    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(SubCategoryActivity.this, 2);
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
                    getPostData(catId, subCatId);
                }
            }
        });
    }

    void getPostData(String catId, String subCatId) {
        binding.loadMore.setVisibility(View.VISIBLE);
        binding.noData.setVisibility(View.GONE); // Hide noData initially
        if (nextPageToken == 0 && adapter != null) {
            adapter.clearItems();
            binding.recyclerview.showShimmerAdapter();
        }

        Call<MainResponse> call = apiService.fetchProductsByCategory(nextPageToken, catId, subCatId);
        call.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MainResponse apiResponse = response.body();
                    if (apiResponse.getStatus().equalsIgnoreCase("success")) {
                        if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                            if (adapter == null) {
                                adapter = new ProductsAdapter(SubCategoryActivity.this, apiResponse.getData(),
                                        new ProductsAdapter.OnCartChangedListener() {
                                            @Override
                                            public void onCartChanged() {
                                                //updateCartBadge();

                                            }
                                        });
                                binding.recyclerview.setAdapter(adapter);
                                binding.recyclerview.hideShimmerAdapter();
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
                        }else {
                            binding.noData.setVisibility(View.VISIBLE);
                            binding.loadMore.setVisibility(View.GONE);
                            binding.recyclerview.hideShimmerAdapter();
                        }
                    }else {
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.loadMore.setVisibility(View.GONE);
                        binding.recyclerview.hideShimmerAdapter();
                    }
                }
                binding.loadMore.setVisibility(View.GONE);
                isLoading = false;
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                binding.noData.setVisibility(View.VISIBLE);
                binding.loadMore.setVisibility(View.GONE);
                binding.recyclerview.hideShimmerAdapter();
                isLoading = false;
            }
        });
    }

    void loadCategory(){
        Call<CategoryResponse> call = apiService.fetchSubCategories(catId);

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
                        if (categoryResponse.getData() != null && !categoryResponse.getData().isEmpty()) {
                            binding.newArrivalTxt.setText("From " + categoryResponse.getData().get(0).getName());
                            binding.noSubCategory.setVisibility(View.GONE);
                        }else {
                            binding.noSubCategory.setVisibility(View.VISIBLE);
                            binding.newArrivalTxt.setText("From " + catName);
                        }
                        CategoryAdapter adapter = new CategoryAdapter(SubCategoryActivity.this,
                                categoryResponse.getData(), "sub_category_tbl", new CategoryAdapter.OnChangeListener() {
                            @Override
                            public void onSelect(CategoryModel categoryModel) {
                                getPostData(catId, categoryModel.getId());
                                binding.newArrivalTxt.setText("From " + categoryModel.getName());
                            }
                        });
                        binding.categoryRec.setAdapter(adapter);
                        binding.categoryRec.hideShimmerAdapter();
                    }else {
                        binding.noSubCategory.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                binding.categoryRec.hideShimmerAdapter();
                binding.noSubCategory.setVisibility(View.VISIBLE);
            }
        });
    }

}