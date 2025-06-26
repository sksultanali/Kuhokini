package com.kuhokini.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kuhokini.APIModels.ProductData;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.Activities.CheckOut;
import com.kuhokini.Activities.ProductDetails;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.R;
import com.kuhokini.TinyCart.CartItem;
import com.kuhokini.TinyCart.TinyCart;
import com.kuhokini.databinding.ItemCartBinding;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    Context context;
    Activity activity;
    ArrayList<VariantResponse.Variant> products;
    CartListener cartListener;
    TinyCart cart;
    ApiService apiService;
    ArrayAdapter<String> obj;
    ArrayList<String> qtyList = new ArrayList<>();

    public interface CartListener {
        public void onQuantityChanged();
    }

    public CartAdapter(Context context, Activity activity, ArrayList<VariantResponse.Variant> products, CartListener cartListener) {
        this.context = context;
        this.activity = activity;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCart.getInstance();
        qtyList.clear();
        qtyList.add("Qty 1 ");
        qtyList.add("Qty 2 ");
        qtyList.add("Qty 3 ");
        qtyList.add("Qty 4 ");
        qtyList.add("Qty 5 ");
        qtyList.add("Qty 6 ");
        qtyList.add("Qty 7 ");
        qtyList.add("Qty 8 ");
        qtyList.add("Qty 9 ");
        qtyList.add("Qty 10 ");
        obj = new ArrayAdapter<String>(activity,
                R.layout.layout_spinner_items, qtyList);
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent,false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        VariantResponse.Variant product = products.get(position);

        if (product.getImages().get(0).getImageUrl() != null && !activity.isDestroyed()){
            Glide.with(context)
                    .load(product.getImages().get(0).getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(context.getDrawable(R.drawable.no_image))
                    .into(holder.binding.image);
        }

        // Get the CartItem to access weight information
        CartItem cartItem = cart.getItems().get(product);
        try {
            holder.binding.name.setText(cartItem.getName());
            //holder.binding.description.setText(String.format("%.2f gm", cartItem.getWeight()));
            holder.binding.description.setText(product.getVarient_name());
        }catch (Exception e){

        }

        holder.binding.normalPrice.setPaintFlags(holder.binding.normalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.normalPrice.setText(String.valueOf(product.getNormal_price()));
        holder.binding.price.setText(String.valueOf(product.getSelling_price()));

        holder.binding.qtySpinner.setAdapter(obj);
        int selectedQty = cartItem.getQuantity();
        if (selectedQty >= 1 && selectedQty <= 10) {
            holder.binding.qtySpinner.setTag("init");
            holder.binding.qtySpinner.setSelection(selectedQty - 1); // index = quantity - 1
        }

        holder.binding.qtySpinner.setTag("init");
        holder.binding.qtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ("init".equals(holder.binding.qtySpinner.getTag())) {
                    holder.binding.qtySpinner.setTag(null); // Reset after first skip
                    return;
                }

                cart.updateItem(product, (position + 1), product.getSelling_price(), product.getWeight());
                cartListener.onQuantityChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        holder.binding.removeItem.setOnClickListener(v->{
            cart.removeItem(product);
            cartListener.onQuantityChanged();
        });

        holder.binding.buyNow.setOnClickListener(v->{
            Intent i = new Intent(activity, CheckOut.class);
            activity.startActivity(i);
        });



    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }
}
