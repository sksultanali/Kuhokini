package com.kuhokini.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kuhokini.APIModels.CouponResponse;
import com.kuhokini.Adapters.CouponAdapter;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityCouponsBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponsActivity extends AppCompatActivity {

    ActivityCouponsBinding binding;
    String couponType;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCouponsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        GridLayoutManager gnm = new GridLayoutManager(CouponsActivity.this, 2);
        binding.recyclerview.setLayoutManager(gnm);
        checkCoupons(null);

        binding.searchTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    checkCoupons(charSequence.toString());
                    binding.closeBtn.setVisibility(View.VISIBLE);
                }else {
                    binding.closeBtn.setVisibility(View.GONE);
                    checkCoupons(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.closeBtn.setOnClickListener(v->{
            binding.searchTags.setText("");
            binding.closeBtn.setVisibility(View.GONE);
            checkCoupons(null);
        });




    }

    void checkCoupons(String keyword){
        //binding.loadMore.setVisibility(View.VISIBLE);
        binding.recyclerview.showShimmerAdapter();

        Call<CouponResponse> call = apiService.getCoupons(null, keyword);
        call.enqueue(new Callback<CouponResponse>() {
            @Override
            public void onResponse(Call<CouponResponse> call, Response<CouponResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    CouponResponse couponResponse = response.body();
                    if (couponResponse.getStatus().equalsIgnoreCase("success")){
                        CouponAdapter adapter1 = new CouponAdapter(CouponsActivity.this, couponResponse.getData());
                        binding.recyclerview.setAdapter(adapter1);
                        binding.recyclerview.setVisibility(View.VISIBLE);
                        binding.noData.setVisibility(View.GONE);
                    }else {
                        binding.recyclerview.setVisibility(View.GONE);
                        binding.noData.setVisibility(View.VISIBLE);
                    }
                    //binding.loadMore.setVisibility(View.GONE);
                    binding.recyclerview.hideShimmerAdapter();
                }
            }

            @Override
            public void onFailure(Call<CouponResponse> call, Throwable t) {
                binding.noData.setVisibility(View.VISIBLE);
                //binding.loadMore.setVisibility(View.GONE);
                binding.recyclerview.hideShimmerAdapter();
                Toast.makeText(CouponsActivity.this, "Coupon: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}