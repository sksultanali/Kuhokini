package com.kuhokini.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kuhokini.APIModels.SearchKeywordResponse;
import com.kuhokini.Adapters.SearchAdapter;
import com.kuhokini.Adapters.SearchHistoryAdapter;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.DB_Helper_Search_History;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivitySearchBinding;
import com.kuhokini.databinding.CustomDialogBinding;
import com.kuhokini.databinding.DialogMessageBoxBinding;
import com.kuhokini.databinding.DialogSortOptionsBinding;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    ApiService apiService;
    String searchKeyword;
    private DB_Helper_Search_History dbHelper;
    ArrayList<String> searchArray = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
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
        dbHelper = new DB_Helper_Search_History(this);

        if (getIntent().hasExtra("keyword")){
            searchKeyword = getIntent().getStringExtra("keyword");
            binding.searchTags.setText(searchKeyword);
            searchKeyword(searchKeyword);
        }

        loadSearchHistory();

        binding.searchTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0){
                    binding.closeBtn.setVisibility(View.GONE);
                    loadSearchHistory();
                }else {
                    binding.closeBtn.setVisibility(View.VISIBLE);
                    searchKeyword(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.closeBtn.setOnClickListener(v->{
            binding.searchTags.setText("");
            binding.closeBtn.setVisibility(View.GONE);
        });








    }

    private void loadSearchHistory() {
        if (dbHelper.getAllSearchQueries() != null){
            searchArray = dbHelper.getAllSearchQueries();
            Collections.reverse(searchArray);
            binding.searchRec.setLayoutManager(new LinearLayoutManager(this));
            SearchHistoryAdapter adapter1 = new SearchHistoryAdapter(SearchActivity.this, searchArray);
            binding.searchRec.setAdapter(adapter1);
        }

        if (searchArray.isEmpty()){
            binding.searchRec.setVisibility(View.GONE);
            binding.noData.setVisibility(View.VISIBLE);
        }else {
            binding.noData.setVisibility(View.GONE);
            binding.searchRec.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    }

    void searchKeyword(String searchKeyword){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.searchRec.setVisibility(View.GONE);
        binding.noData.setVisibility(View.GONE);

        Call<SearchKeywordResponse> call = apiService.fetchProductNamesOnly(searchKeyword);
        call.enqueue(new Callback<SearchKeywordResponse>() {
            @Override
            public void onResponse(Call<SearchKeywordResponse> call, Response<SearchKeywordResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    SearchKeywordResponse keywordResponse = response.body();
                    if (keywordResponse.getStatus().equalsIgnoreCase("success")){
                        binding.searchRec.setVisibility(View.VISIBLE);
                        SearchAdapter searchAdapter = new SearchAdapter(SearchActivity.this, keywordResponse.getData());
                        binding.searchRec.setAdapter(searchAdapter);
                    }else {
                        binding.noData.setVisibility(View.VISIBLE);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<SearchKeywordResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

}