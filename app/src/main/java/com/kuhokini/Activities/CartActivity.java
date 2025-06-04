package com.kuhokini.Activities;

import androidx.appcompat.app.AppCompatActivity;
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

import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.Adapters.CartAdapter;
import com.kuhokini.databinding.ActivityCartBinding;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    ArrayList<VariantResponse.Variant> products;
    Activity activity;
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//
        products = new ArrayList<>();

        Cart cart = TinyCartHelper.getCart();
        activity = CartActivity.this;

        for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            VariantResponse.Variant product = (VariantResponse.Variant) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }

        if (products.isEmpty()){
            binding.cartEmpty.setVisibility(View.VISIBLE);
        }


        adapter = new CartAdapter(CartActivity.this, activity, products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subtotal.setText(String.format("₹%.2f",cart.getTotalPrice()));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(CartActivity.this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(CartActivity.this, layoutManager.getOrientation());
        binding.cartList.setLayoutManager(layoutManager);
//        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setAdapter(adapter);

        binding.subtotal.setText(String.format("₹%.2f",cart.getTotalPrice()));

        binding.clearAllBtn.setOnClickListener(v->{
            if (cart.isCartEmpty()){
                Toast.makeText(activity, "Already Empty", Toast.LENGTH_SHORT).show();
            }else {
                cart.clearCart();
                products.clear();
                binding.cartEmpty.setVisibility(View.VISIBLE);
                binding.subtotal.setText("₹0.00");
                adapter.notifyDataSetChanged();
            }
        });

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });


        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cart.isCartEmpty()){
                    Intent i = new Intent(CartActivity.this, CheckOut.class);
                    startActivity(i);
                }else {
                    Toast.makeText(activity, "cart is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}