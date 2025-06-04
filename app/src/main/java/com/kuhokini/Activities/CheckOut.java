package com.kuhokini.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.Account.Login;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.databinding.ActivityCheckOutBinding;
import com.kuhokini.databinding.AddressDialogBoxBinding;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class CheckOut extends AppCompatActivity {

    ActivityCheckOutBinding binding;
    Cart cart;
//    ArrayList<boughtModel> products;
//    ProductModel productModel;
//    FirebaseAuth auth;
//    FirebaseDatabase database;
//    FirebaseStorage storage;
    Uri imageUri;
    int money;
    Activity activity;
    ProgressDialog dialog, addDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.goBack.setOnClickListener(v->onBackPressed());

//        database = FirebaseDatabase.getInstance();
//        auth = FirebaseAuth.getInstance();
//        storage = FirebaseStorage.getInstance();
//        activity = CheckOut.this;
//
//        dialog = new ProgressDialog(CheckOut.this);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setTitle("Order Processing");
//        dialog.setMessage("don't exit or close app");
//
//        addDialog = new ProgressDialog(CheckOut.this);
//        addDialog.setMessage("saving data");
//        addDialog.setCancelable(false);
//        addDialog.setCanceledOnTouchOutside(false);

        cart = TinyCartHelper.getCart();
//        products = new ArrayList<>();
//        products.clear();
//
//        for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
//            productModel = (ProductModel) item.getKey();
//            int quantity = item.getValue();
//            productModel.setQuantity(quantity);
//
//            products.add(new boughtModel(productModel.getId(), productModel.getQuantity(),
//                    productModel.getDiscountedPrice().intValue(), "Order is processing"));
//
//        }

        String amount = String.valueOf(cart.getTotalPrice());
//        binding.subTotal.setText(amount);
//        binding.total.setText(amount);
//        binding.paymentAmount.setText(amount);
//        binding.payNowText.setText("Pay ₹" + amount + " Now");
//
//        binding.editAddress.setOnClickListener(v->{
//            addressInput();
//        });

//        binding.uploadScreenShot.setOnClickListener(v->{
//            if (binding.address.getText().toString().equalsIgnoreCase("edit your address!")){
//                addressInput();
//            }else {
//                ImagePicker.with(this)
//                        .crop()	    			//Crop image(Optional), Check Customization for more option
//                        .compress(3072)			//Final image size will be less than 3 MB(Optional)
//                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                        .start(85);
//            }
//        });


//        binding.payBtn.setOnClickListener(v->{
//
//            int paidValue = Integer.parseInt(binding.paymentAmount.getText().toString());
//
//        });

        binding.payNow.setOnClickListener(v->{
            Helper.showActionDialog(CheckOut.this, "Login Required",
                    "Please login to place order. Click below login button.",
                    "Login", "Later", true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {
                            startActivity(new Intent(CheckOut.this, Login.class));
                        }

                        @Override
                        public void onNoButtonClicked() {

                        }

                        @Override
                        public void onCloseButtonClicked() {

                        }
                    });
        });

    }

    public void addressInput(){
        AddressDialogBoxBinding addressBinding = AddressDialogBoxBinding.inflate(getLayoutInflater());

        // Create a new dialog and set the custom layout
        Dialog dialog = new Dialog(this);
        dialog.setContentView(addressBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        addressBinding.nameInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                addressBinding.nameInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(addressBinding.nameInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);

        addressBinding.postBtn.setOnClickListener(v->{
            String name = addressBinding.nameInput.getText().toString();
            String phone = addressBinding.nameInput.getText().toString();
            String address = addressBinding.nameInput.getText().toString();
            String pinCode = addressBinding.nameInput.getText().toString();

            if (name.isEmpty()){
                addressBinding.nameInput.setError("*");
            }else if (phone.isEmpty()){
                addressBinding.phone.setError("*");
            }else if (address.isEmpty()){
                addressBinding.addressBox.setError("*");
            }else if (pinCode.isEmpty()){
                addressBinding.pinCode.setError("*");
            }else {

                String makeAddress = "Name: " + addressBinding.nameInput.getText().toString() + "\n" +
                        "Phone: " + addressBinding.phone.getText().toString() + "\n\n" +
                        addressBinding.addressBox.getText().toString() + "\n" +
                        "Pin-code: " + addressBinding.pinCode.getText().toString();

//                binding.address.setText(makeAddress);

                addDialog.show();

            }
        });






        // Show the dialog
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getData() != null && requestCode == 85){
//            binding.uploadScreenShot.setEnabled(false);
            imageUri = data.getData();
            dialog.show();
        }

        if (requestCode == 17) {
            if (resultCode == RESULT_OK) {
                // Payment was successful, you can handle it here.
                // You can also check the payment response data for more details.
                
                if (data != null){
                    String response = data.getStringExtra("response");

                    if (response == null){
                        Toast.makeText(this, "response is empty", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(this, "data is null", Toast.LENGTH_SHORT).show();
                }
                

            } else if (resultCode == RESULT_CANCELED) {
                // Payment was canceled by the user.
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();

            } else {
                // Payment failed or an unknown error occurred.
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();

            }
        }

    }

}