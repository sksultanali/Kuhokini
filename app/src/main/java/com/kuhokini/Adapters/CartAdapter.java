package com.kuhokini.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.APIModels.ProductData;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.R;
import com.kuhokini.databinding.ItemCartBinding;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    Context context;
    Activity activity;
    ArrayList<VariantResponse.Variant> products;
    CartListener cartListener;
    Cart cart;

    public interface CartListener {
        public void onQuantityChanged();
    }

    public CartAdapter(Context context, Activity activity, ArrayList<VariantResponse.Variant> products, CartListener cartListener) {
        this.context = context;
        this.activity = activity;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
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

        if (product.getImages().get(0) != null && !activity.isDestroyed()){
            Glide.with(context)
                    .load(product.getImages().get(0))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(context.getDrawable(R.drawable.no_image))
                    .into(holder.binding.image);
        }

//        holder.binding.name.setText(product.getName());
//        holder.binding.rating.setText(product.getRating() + " • Ratings • Best");
//        holder.binding.otherNote.setText(product.getOtherNotes());
//        holder.binding.price.setText("₹" + product.getDiscountedPrice());
//        holder.binding.normalPrice.setPaintFlags((int) (product.getDiscountedPrice() | Paint.STRIKE_THRU_TEXT_FLAG));
//        holder.binding.normalPrice.setText("₹" + product.getPrice());
//        holder.binding.quantity.setText(product.getQuantity() + " item(s)");
//
//
//        holder.itemView.setOnClickListener(v->{
//            DialogQuantityBinding quantityDialogBinding = DialogQuantityBinding.inflate(LayoutInflater.from(context));
//
//            AlertDialog dialog = new AlertDialog.Builder(context)
//                    .setView(quantityDialogBinding.getRoot())
//                    .create();
//
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
//
//            quantityDialogBinding.productName.setText(product.getName());
////            quantityDialogBinding.productStock.setText("Stock: " + product.getStock());
////            quantityDialogBinding.quantity.setText(String.valueOf(product.getQuantity()));
////            int stock = product.getStock();
//
//
//            quantityDialogBinding.plusBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int quantity = product.getQuantity();
//                    quantity++;
//
//                    product.setQuantity(quantity);
//                    quantityDialogBinding.quantity.setText(String.valueOf(quantity));
//
//                    notifyDataSetChanged();
//                    cart.updateItem(product, product.getQuantity());
//                    cartListener.onQuantityChanged();
//                }
//            });
//
//            quantityDialogBinding.minusBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int quantity = product.getQuantity();
//                    if(quantity > 1)
//                        quantity--;
//                    product.setQuantity(quantity);
//                    quantityDialogBinding.quantity.setText(String.valueOf(quantity));
//
//                    notifyDataSetChanged();
//                    cart.updateItem(product, product.getQuantity());
//                    cartListener.onQuantityChanged();
//                }
//            });
//
//            quantityDialogBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
////                        notifyDataSetChanged();
////                        cart.updateItem(product, product.getQuantity());
////                        cartListener.onQuantityChanged();
//                }
//            });
//
//            dialog.show();
//        });

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
