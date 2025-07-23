package com.kuhokini.Activities;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.hishd.tinycart.model.Cart;
//import com.hishd.tinycart.model.Item;
//import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.APIModels.CategoryResponse;
import com.kuhokini.APIModels.CouponResponse;
import com.kuhokini.Account.Login;
import com.kuhokini.Adapters.AddressAdapter;
import com.kuhokini.Adapters.CategoryAdapter;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.Precautions;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.Models.AddressResponse;
import com.kuhokini.Models.CouponModel;
import com.kuhokini.R;
import com.kuhokini.TinyCart.TinyCart;
import com.kuhokini.databinding.ActivityCheckOutBinding;
import com.kuhokini.databinding.AddressDialogBoxBinding;
import com.kuhokini.databinding.DialogListShowBinding;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOut extends AppCompatActivity {

    ActivityCheckOutBinding binding;
    ApiService apiService;
    TinyCart cart;
    ProgressDialog progressDialog;
    int price, weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());
        progressDialog = new ProgressDialog(CheckOut.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Connecting server...");

        price = getIntent().getIntExtra("totalAmt", 0);
        weight = getIntent().getIntExtra("totalWeight", 0);

        Toast.makeText(this, price + " : " +weight, Toast.LENGTH_SHORT).show();

        cart = TinyCart.getInstance();
        binding.payNow.setOnClickListener(v->{
            String phoneNo = Helper.getData(CheckOut.this, "phone");
            String name = binding.name.getText().toString();
            String address = binding.address.getText().toString();
            String phone = binding.phone.getText().toString();
            
            if (phoneNo == null || phoneNo.isEmpty()){
                Helper.showLoginDialog(CheckOut.this);
            }else if (name.isEmpty() || address.isEmpty() || phone.isEmpty()){
                Helper.showOnlyMessage(CheckOut.this, "Address Required",
                        "Please click on <b>'Add New Address'</b> and fill complete address where you want your delivery!");
            }else {
                Toast.makeText(this, "good to go", Toast.LENGTH_SHORT).show();
            }
        });

        loadAddresses();
        binding.addAddress.setOnClickListener(v -> {
            String phoneNo = Helper.getData(CheckOut.this, "phone");
            if (phoneNo == null || phoneNo.isEmpty()){
                Helper.showLoginDialog(CheckOut.this);
            }else {
                startActivity(new Intent(CheckOut.this, AddressActivity.class));
            }
        });

        binding.payToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (binding.payOnline.isChecked()){
                    binding.paymentIcons.setVisibility(View.VISIBLE);
                    binding.cashExtra.setVisibility(View.GONE);
                }else {
                    binding.paymentIcons.setVisibility(View.GONE);
                    binding.cashExtra.setVisibility(View.VISIBLE);
                }
            }
        });




        binding.applyCode.setOnClickListener(v -> {
            String couponCode = binding.ccEdText.getText().toString().trim();
            if (couponCode.isEmpty()) {
                binding.ccEdText.setError("Required!");
                return;
            }

            Call<CouponResponse> call = apiService.checkCouponCode(couponCode);
            call.enqueue(new Callback<CouponResponse>() {
                @Override
                public void onResponse(Call<CouponResponse> call, Response<CouponResponse> response) {
                    if (response.isSuccessful() && response.body() != null){
                        CouponResponse couponResponse = response.body();
                        if (couponResponse.getStatus().equalsIgnoreCase("success")){
                            applyCoupon(couponResponse.getSingleData());
                        }else {
                            Helper.showOnlyMessage(CheckOut.this, "Failed ",
                                    couponResponse.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CouponResponse> call, Throwable t) {
                    Helper.showOnlyMessage(CheckOut.this, "Error ",
                            "Something went wrong. Please try again later. "+ t.getLocalizedMessage());
                }
            });
        });

        binding.removeCuppon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.appliedCoupon.setVisibility(View.GONE);
                binding.PerDis.setText("");
//                binding.coupons.setTextColor(getColor(R.color.icon_color));
//                binding.coupons.setText(" | Coupon can be applied");
//                Helper.COUPON_CODE = 0;
//                Helper.FLAT_DISCOUNT = 0;
//                binding.ccEdText.setText("");
//                setPrice();
            }
        });

    }

    private void setPrice() {
        int fPrice = price;
        binding.basePrice.setText(price+"");

        if (price >= Precautions.FREE_DELIVERY_AFTER) {
            fPrice += 0;
            binding.deliveryCharge.setText("0");
        }else {
            if (Precautions.REGULAR_DELIVERY) {
                fPrice += Precautions.SURFACE_DELIVERY_CHARGES;
                binding.deliveryCharge.setText(Precautions.SURFACE_DELIVERY_CHARGES+"");
                //binding.extraNote.setText("Delivery charges ₹" + Precautions.SURFACE_DELIVERY_CHARGES + " only");
            }else {
                fPrice += Precautions.EXPRESS_DELIVERY_CHARGES;
                binding.deliveryCharge.setText(Precautions.EXPRESS_DELIVERY_CHARGES+"");
                //binding.extraNote.setText("Delivery charges ₹" + Precautions.EXPRESS_DELIVERY_CHARGES + " only");
            }
            //binding.freeDelivery.setText("Delivery charges ");
            //binding.deliveryPrice.setVisibility(View.VISIBLE);
        }

        if (Precautions.CASH_PAYMENT) {
            fPrice += Precautions.CASH_PAYMENT_CHARGES;
            binding.codAmountText.setText(Precautions.CASH_PAYMENT_CHARGES+"");
            binding.codChargesLayout.setVisibility(View.VISIBLE);
        }else {
            binding.codChargesLayout.setVisibility(View.GONE);
        }

        //binding.subtotal.setText(String.valueOf(fPrice));
        binding.sellingPrice.setText(String.valueOf(fPrice));
    }

    private void applyCoupon(CouponModel coupon) {
//        if ("Percentage".equalsIgnoreCase(coupon.getType())) {
//            Helper.FLAT_DISCOUNT = 0;
//            Helper.COUPON_CODE = coupon.getPercentageOrAmount();
//            binding.coupons.setText(" | " + Helper.COUPON_CODE + "% Coupon code applied");
//            binding.PerDis.setText(Helper.COUPON_CODE + "% OFF applied");
//        } else {
//            Helper.COUPON_CODE = -1;
//            Helper.FLAT_DISCOUNT = coupon.getPercentageOrAmount();
//            binding.coupons.setText(" | ₹" + Helper.FLAT_DISCOUNT + "/- Flat Off");
//            binding.PerDis.setText("₹" + Helper.FLAT_DISCOUNT + "/- Flat Off");
//        }
//
//        binding.appliedCoupon.setVisibility(View.VISIBLE);
//        binding.coupons.setTextColor(getColor(R.color.blue_purple));
//        setPrice();
    }

    private void loadAddresses() {
        String phoneNo = Helper.getData(CheckOut.this, "user_id");
        if (phoneNo == null || phoneNo.isEmpty()){
            noAddress();
            return;
        }
        Call<AddressResponse> call = apiService.getAddresses(phoneNo);
        call.enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    AddressResponse addressResponse = response.body();
                    if (addressResponse.getCod_enable() == 0){
                        binding.cod.setChecked(false);
                        binding.payOnline.setChecked(true);
                        binding.cod.setVisibility(View.GONE);
                    }

                    if (addressResponse.getStatus().equalsIgnoreCase("success")){
                        if (addressResponse.getAddresses() == null || addressResponse.getAddresses().isEmpty()){
                            noAddress();
                        }else {
                            AddressResponse.AddressModel addressModel = addressResponse.getAddresses().get(0);
                            binding.name.setText(addressModel.getName());
                            binding.phone.setText("+91 "+addressModel.getPhone());
                            if (addressModel.getLandmark() != null && !addressModel.getLandmark().isEmpty()) {
                                binding.address.setText(addressModel.getAddress() + ", "+ addressModel.getLandmark()+", "
                                        + addressModel.getState());
                            }else {
                                binding.address.setText(addressModel.getAddress() + ", "
                                        + addressModel.getState());
                            }
                            binding.noAddress.setVisibility(View.GONE);
                            binding.addressLayout.setVisibility(View.VISIBLE);
                            binding.changeAddress.setVisibility(View.VISIBLE);
                            binding.addAddress.setVisibility(View.GONE);

                            binding.changeAddress.setOnClickListener(v->{
                                showAddresses(addressResponse.getAddresses());
                            });
                        }
                    }else {
                        noAddress();
                    }
                }else {
                    noAddress();
                }
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                noAddress();
            }
        });
    }

    private void noAddress() {
        binding.noAddress.setVisibility(View.VISIBLE);
        binding.addressLayout.setVisibility(View.GONE);
        binding.changeAddress.setVisibility(View.GONE);
        binding.addAddress.setVisibility(View.VISIBLE);
    }

    private void showAddresses(List<AddressResponse.AddressModel> addressModelList) {
        DialogListShowBinding listsBinding = DialogListShowBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(CheckOut.this);
        dialog.setContentView(listsBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        listsBinding.title.setText("Select Any Address");

        listsBinding.recyclerview.setLayoutManager(new LinearLayoutManager(CheckOut.this));
        AddressAdapter addressAdapter = new AddressAdapter(CheckOut.this,
                addressModelList,
                apiService, progressDialog, new AddressAdapter.OnChangeListener() {
            @Override
            public void onChangePrimary() {
                
            }

            @Override
            public void onSelect(AddressResponse.AddressModel addressModel) {
                binding.name.setText(addressModel.getName());
                binding.phone.setText("+91 "+addressModel.getPhone());
                if (addressModel.getLandmark() != null && !addressModel.getLandmark().isEmpty()) {
                    binding.address.setText(addressModel.getAddress() + ", "+ addressModel.getLandmark()+", "
                            + addressModel.getState());
                }else {
                    binding.address.setText(addressModel.getAddress() + ", "
                            + addressModel.getState());
                }
                dialog.dismiss();
            }
        });

        listsBinding.recyclerview.setAdapter(addressAdapter);
        listsBinding.noData.setVisibility(View.GONE);
        listsBinding.addNewAddress.setVisibility(View.VISIBLE);

        listsBinding.addNewAddressBtn.setOnClickListener(v->{
            startActivity(new Intent(CheckOut.this, AddressActivity.class));
            dialog.dismiss();
        });

        listsBinding.closeBtn.setOnClickListener(v->{
            dialog.dismiss();
        });
        
        dialog.show();
    }


}