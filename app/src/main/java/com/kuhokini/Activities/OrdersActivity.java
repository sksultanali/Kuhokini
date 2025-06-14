package com.kuhokini.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kuhokini.Adapters.OrdersAdapter;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Models.OrderModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityOrdersBinding;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    ActivityOrdersBinding binding;
    List<OrderModel> models;
    int nextPageToken, currentPage = 0;
    String keyword;
    OrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(OrdersActivity.this));

        models = new ArrayList<>();
        models.add(new OrderModel("Product A", "Sk Sultan Ali", "Success", "599",
                "https://assets.ajio.com/medias/sys_master/root/20240430/Rkdf/6630daf805ac7d77bb33f855/-473Wx593H-6005364530-multi-MODEL.jpg"));
        models.add(new OrderModel("Product B","Ramesh Poria", "Failed", "1099",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRR6vSfMpr6Zkdw_0VI1g6K6Szb2rPSWiXEbg&s"));
        models.add(new OrderModel("Product C","Koushik Mojumdar", "Pending", "712",
                "https://www.giva.co/cdn/shop/files/PD0218_1_18ac8de0-3c62-4ab1-87b3-601ef392314e.jpg?v=1712927159"));
        models.add(new OrderModel("Product D","Koushik Mojumdar", "Cancelled", "1409",
                "https://www.giva.co/cdn/shop/files/preview_images/b735876f2f9b47ae9a42389082931066.thumbnail.0000000000.jpg?v=1706783593&width=1946"));

        binding.dateRange.setOnClickListener(v->{
            Helper.showDateRangePicker(getSupportFragmentManager(),
                    "dd LLL",
                    "dd LLL, yyyy",
                    (startDate, endDate) -> {
                        binding.dateTextView.setText(startDate + " - " + endDate);
                    }
            );
        });

        adapter = new OrdersAdapter(OrdersActivity.this, models);
        binding.recyclerview.setAdapter(adapter);
        binding.loadMore.setVisibility(View.GONE);

        binding.searchTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    keyword = charSequence.toString();
                    binding.closeBtn.setVisibility(View.VISIBLE);
                }else {
                    binding.closeBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.closeBtn.setOnClickListener(v->{
            binding.searchTags.setText("");
            binding.closeBtn.setVisibility(View.GONE);
            nextPageToken = 0; keyword = null;
        });






    }
}