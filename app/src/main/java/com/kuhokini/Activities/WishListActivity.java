package com.kuhokini.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kuhokini.Adapters.WishLIstAdapter;
import com.kuhokini.Helpers.DB_Helper_WishList;
import com.kuhokini.Models.WishListModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityWishListBinding;

import java.util.ArrayList;

public class WishListActivity extends AppCompatActivity {

    ActivityWishListBinding binding;
    DB_Helper_WishList db_wishList_helper;
    ArrayList<WishListModel> wishListModelArrayList;
    WishLIstAdapter wisAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWishListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());

        db_wishList_helper = new DB_Helper_WishList(WishListActivity.this);
        wishListModelArrayList = new ArrayList<>();

        // Retrieve all wishlist from the database
        ArrayList<WishListModel> searchModelArrayList = db_wishList_helper.getAllWishList();
        wishListModelArrayList.clear();
        for (WishListModel searchQuery : searchModelArrayList) {
            // Do something with each search query
            WishListModel wishListModel = new WishListModel();

            wishListModel.setId(searchQuery.getId());
            wishListModel.setName(searchQuery.getName());
            wishListModelArrayList.add(wishListModel);
        }

        GridLayoutManager lnm2 = new GridLayoutManager(WishListActivity.this, 2);
        binding.wishListRec.setLayoutManager(lnm2);

        if (wishListModelArrayList != null){
            wisAdapter = new WishLIstAdapter(WishListActivity.this, wishListModelArrayList, binding.noData);
            binding.wishListRec.setAdapter(wisAdapter);
            binding.noData.setVisibility(View.GONE);
        }
        if (wishListModelArrayList.isEmpty()){
            binding.wishListRec.setVisibility(View.GONE);
            binding.noData.setVisibility(View.VISIBLE);
        }else {
            binding.wishListRec.setVisibility(View.VISIBLE);
            binding.noData.setVisibility(View.GONE);
        }

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });





    }
}