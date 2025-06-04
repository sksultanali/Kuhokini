package com.kuhokini.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kuhokini.Helpers.Helper;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityMenuBinding;

import org.commonmark.node.OrderedList;

public class MenuActivity extends AppCompatActivity {

    ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.myProfile.setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
        });

        binding.addresses.setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, AddressActivity.class));
        });

        binding.wishList.setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, WishListActivity.class));
        });
        binding.orders.setOnClickListener(v->{
            //startActivity(new Intent(MenuActivity.this, OrderedList.class));
        });
        binding.coupons.setOnClickListener(v->{
            //startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
        });

        binding.rate.setOnClickListener(v->{
            //startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
        });

        binding.tc.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "");
        });
        binding.pp.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "");
        });
        binding.rp.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "");
        });
        binding.facebook.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "");
        });
        binding.instagram.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "");
        });
        binding.website.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "");
        });








    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.l2s, R.anim.r2s);
    }
}