package com.kuhokini.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.MainActivity;
import com.kuhokini.R;
import com.kuhokini.TinyCart.TinyCart;
import com.kuhokini.databinding.ActivitySearchResultBinding;
import com.kuhokini.databinding.DialogFilterOptionsBinding;
import com.kuhokini.databinding.DialogSortOptionsBinding;

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

    @SuppressLint("ResourceAsColor")
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


        binding.sort.setOnClickListener(v->{
            DialogSortOptionsBinding listsBinding = DialogSortOptionsBinding.inflate(LayoutInflater.from(SearchResult.this));

            AlertDialog dialog = new AlertDialog.Builder(SearchResult.this)
                    .setView(listsBinding.getRoot())
                    .create();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            if (Helper.SORT_OPTION != null && !Helper.SORT_OPTION.isEmpty()){
                if (Helper.SORT_OPTION.equalsIgnoreCase("Relevant")){
                    listsBinding.relevant.setChecked(true);
                }else if (Helper.SORT_OPTION.equalsIgnoreCase("LowToHigh")){
                    listsBinding.priceLowHigh.setChecked(true);
                }else if (Helper.SORT_OPTION.equalsIgnoreCase("HighToLow")){
                    listsBinding.priceHighLow.setChecked(true);
                }
            }else {
                listsBinding.sortGroup.clearCheck();
            }

            listsBinding.sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (listsBinding.relevant.isChecked()){
                        Helper.SORT_OPTION = "Relevant";
                    } else if (listsBinding.priceLowHigh.isChecked()) {
                        Helper.SORT_OPTION = "LowToHigh";
                    }else if (listsBinding.priceHighLow.isChecked()) {
                        Helper.SORT_OPTION = "HighToLow";
                    }else{
                        Helper.SORT_OPTION = "Relevant";
                    }
                    nextPageToken = 0;
                    getPostData();
                    binding.sortTxt.setText("Sort: " + Helper.SORT_OPTION);
                    dialog.dismiss();
                }
            });

            listsBinding.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        });
        binding.filter.setOnClickListener(v->{
            DialogFilterOptionsBinding listsBinding = DialogFilterOptionsBinding.inflate(LayoutInflater.from(SearchResult.this));

            AlertDialog dialog = new AlertDialog.Builder(SearchResult.this)
                    .setView(listsBinding.getRoot())
                    .create();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            if (Helper.FILTER_OPTION != null && !Helper.FILTER_OPTION.isEmpty()){
                if (Helper.FILTER_OPTION.equalsIgnoreCase("Under 500")){
                    listsBinding.under500.setChecked(true);
                }else if (Helper.FILTER_OPTION.equalsIgnoreCase("Under 1000")){
                    listsBinding.under1000.setChecked(true);
                }else if (Helper.FILTER_OPTION.equalsIgnoreCase("Under 1500")){
                    listsBinding.under1500.setChecked(true);
                }else if (Helper.FILTER_OPTION.equalsIgnoreCase("Under 2000")){
                    listsBinding.under2000.setChecked(true);
                }else if (Helper.FILTER_OPTION.equalsIgnoreCase("ShowAll")){
                    listsBinding.showAll.setChecked(true);
                }
            }else {
                listsBinding.sortGroup.clearCheck();
            }

            listsBinding.sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    binding.filterTxt.setText("Filtered");
                    if (listsBinding.under500.isChecked()){
                        Helper.FILTER_OPTION = "Under 500";
                    } else if (listsBinding.under1000.isChecked()) {
                        Helper.FILTER_OPTION = "Under 1000";
                    }else if (listsBinding.under1500.isChecked()) {
                        Helper.FILTER_OPTION = "Under 1500";
                    }else if (listsBinding.under2000.isChecked()) {
                        Helper.FILTER_OPTION = "Under 2000";
                    }else if (listsBinding.showAll.isChecked()) {
                        Helper.FILTER_OPTION = "ShowAll";
                        binding.filterTxt.setText("Filter");
                    }
                    nextPageToken = 0;
                    getPostData();
                    dialog.dismiss();
                }
            });

            listsBinding.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        });

    }

    private String buildExtraQuery() {
        StringBuilder extraQuery = new StringBuilder();

        if (Helper.FILTER_OPTION != null && !Helper.FILTER_OPTION.isEmpty()) {
            switch (Helper.FILTER_OPTION.toLowerCase()) {
                case "under 500":
                    extraQuery.append(" AND v.selling_price < 500");
                    break;
                case "under 1000":
                    extraQuery.append(" AND v.selling_price < 1000");
                    break;
                case "under 1500":
                    extraQuery.append(" AND v.selling_price < 1500");
                    break;
                case "under 2000":
                    extraQuery.append(" AND v.selling_price < 2000");
                    break;
                case "showall":
                    //extraQuery.append("");
                    break;
                default:
                    extraQuery.append(" AND v.selling_price < 500");
                    break;
            }
        }

//        if (Helper.SORT_OPTION != null && !Helper.SORT_OPTION.isEmpty()) {
//            switch (Helper.SORT_OPTION.toLowerCase()) {
//                case "hightolow":
//                    extraQuery.append(" ORDER BY v.selling_price desc");
//                    break;
//                case "lowtohigh":
//                    extraQuery.append(" ORDER BY v.selling_price asc");
//                    break;
//                default:
//                    //extraQuery.append("");
//                    break;
//            }
//        }

        return extraQuery.length() > 0 ? extraQuery.toString() : "";
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

        if (Helper.SORT_OPTION == null){
            Helper.SORT_OPTION = "R";
        }

        String sOp = Helper.SORT_OPTION.toLowerCase();

        Call<MainResponse> call = apiService.fetchProducts(nextPageToken, keyword, sOp, buildExtraQuery());
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