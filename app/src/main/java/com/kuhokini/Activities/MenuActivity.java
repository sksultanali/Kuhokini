package com.kuhokini.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kuhokini.Account.Login;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityMenuBinding;

import org.commonmark.node.OrderedList;

public class MenuActivity extends AppCompatActivity {

    ActivityMenuBinding binding;
    String userId;

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
        userId = Helper.getData(MenuActivity.this, "user_id");

        if (userId == null){
            binding.myProfile.setVisibility(View.GONE);
            binding.notLoginSec.setVisibility(View.VISIBLE);
        }else {
            binding.myProfile.setVisibility(View.VISIBLE);
            binding.notLoginSec.setVisibility(View.GONE);
        }

        binding.myProfile.setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
        });

        binding.logBtn.setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, Login.class));
            finish();
        });

        binding.addresses.setOnClickListener(v->{
            if (userId == null){
                Helper.showLoginDialog(MenuActivity.this);
            }else {
                startActivity(new Intent(MenuActivity.this, AddressActivity.class));
            }
        });

        binding.wishList.setOnClickListener(v->{
            startActivity(new Intent(MenuActivity.this, WishListActivity.class));
        });
        binding.orders.setOnClickListener(v->{
            if (userId == null){
                Helper.showLoginDialog(MenuActivity.this);
            }else {
                startActivity(new Intent(MenuActivity.this, OrdersActivity.class));
            }
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
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/terms-and-conditions.php");
        });
        binding.pp.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/privacy_policy.php");
        });
        binding.rp.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/refund-policy.php");
        });

        binding.facebook.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://www.facebook.com/kuhokinijewellery/");
        });
        binding.instagram.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://www.instagram.com/kuhokiniofficial/");
        });
        binding.youTube.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://www.youtube.com/@Kuhokini_official");
        });
        binding.website.setOnClickListener(v->{
            Helper.openLink(MenuActivity.this, "https://kuhokini.com/");
        });

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Long versionCode = (long) packageInfo.versionCode;
            binding.appVersionText.setText("App Version " + versionCode + "\nMade with ❤\uFE0F Kuhokini");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }







    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.l2s, R.anim.r2s);
    }
}