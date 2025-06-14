package com.kuhokini.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kuhokini.Adapters.NotificationAdapter;
import com.kuhokini.Models.NotificationModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityNotificationBinding;

import java.util.ArrayList;
import java.util.Date;

public class NotificationActivity extends AppCompatActivity {

    ActivityNotificationBinding binding;
    ArrayList<NotificationModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());

        arrayList.clear();
        arrayList.add(new NotificationModel("Welcome to our Kuhokini app. We are 24 x 7 available for your service. Thank you choosing Kuhokini",
                new Date().getTime(), false));
        arrayList.add(new NotificationModel("This is a sample message for testing out the notification system!",
                new Date().getTime(), true));

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerview.getContext(), LinearLayoutManager.VERTICAL);
        binding.recyclerview.addItemDecoration(dividerItemDecoration);
        NotificationAdapter adapter = new NotificationAdapter(this, arrayList);
        binding.recyclerview.setAdapter(adapter);

        binding.recyclerview.setVisibility(View.VISIBLE);
        binding.noData.setVisibility(View.GONE);





    }
}