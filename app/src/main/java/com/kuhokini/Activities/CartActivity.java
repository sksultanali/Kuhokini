package com.kuhokini.Activities;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.hishd.tinycart.model.Cart;
//import com.hishd.tinycart.model.Item;
//import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.Adapters.CartAdapter;
import com.kuhokini.R;
import com.kuhokini.TinyCart.CartItem;
import com.kuhokini.TinyCart.TinyCart;
import com.kuhokini.databinding.ActivityCartBinding;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartListener{

    ActivityCartBinding binding;
    ArrayList<VariantResponse.Variant> products;
    Activity activity;
    CartAdapter adapter;
    TinyCart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        cart = TinyCart.getInstance();
        products = new ArrayList<>();
        activity = this;

        refresh();

        // Clear cart button
        binding.clearAllBtn.setOnClickListener(v -> {
            if (cart.getItems().isEmpty()) {
                Toast.makeText(activity, "Already Empty", Toast.LENGTH_SHORT).show();
            } else {
                cart.clearCart();
                products.clear();
                binding.cartEmpty.setVisibility(View.VISIBLE);
                refresh();
                adapter.notifyDataSetChanged();
            }
        });

        // Back button
        binding.goBack.setOnClickListener(v -> onBackPressed());

        // Continue to checkout button
        binding.continueBtn.setOnClickListener(view -> {
            if (!cart.getItems().isEmpty()) {
                Intent i = new Intent(CartActivity.this, CheckOut.class);
                startActivity(i);
            } else {
                Toast.makeText(activity, "Cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void refresh(){
        TinyCart cart = TinyCart.getInstance();
        products.clear();

        // Load cart items
        for (Map.Entry<Object, CartItem> entry : cart.getItems().entrySet()) {
            VariantResponse.Variant product = (VariantResponse.Variant) entry.getKey();
            CartItem cartItem = entry.getValue();

            // Update product with cart quantity
            product.setQuantity(cartItem.getQuantity());
            products.add(product);
        }

        // Show empty state if cart is empty
        if (products.isEmpty()) {
            binding.cartEmpty.setVisibility(View.VISIBLE);
        }

        // Initialize adapter
        adapter = new CartAdapter(this, activity, products, CartActivity.this);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.setAdapter(adapter);

        // Initial total update
        binding.subtotal.setText(String.format("₹%.2f", cart.getTotalPrice()));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onQuantityChanged() {
        refresh();
    }
}