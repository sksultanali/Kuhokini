package com.kuhokini.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());

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
            startActivity(new Intent(MenuActivity.this, OrdersActivity.class));
        });
        binding.coupons.setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, CouponsActivity.class));
        });

        binding.rate.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        binding.tc.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/");
        });
        binding.pp.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/");
        });
        binding.rp.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/");
        });

        binding.facebook.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/");
        });
        binding.instagram.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/");
        });
        binding.website.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/");
        });








    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.l2s, R.anim.r2s);
    }
}