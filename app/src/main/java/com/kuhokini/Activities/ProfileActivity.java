package com.kuhokini.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kuhokini.APIModels.SingleUserResponse;
import com.kuhokini.APIModels.UserModel;
import com.kuhokini.Account.Login;
import com.kuhokini.Helpers.ApiResponse;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.MainActivity;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityProfileBinding;
import com.kuhokini.databinding.CustomDialogBinding;
import com.kuhokini.databinding.DialogEditBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    ApiService apiService;
    ProgressDialog progressDialog;
    private Calendar selectedCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("updating profile ...");
        progressDialog.setCancelable(false);

        getUserDetails();
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Long versionCode = (long) packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            binding.version.setText(versionName + " (" + versionCode+")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        binding.ordersCount.setOnClickListener(v->{
            startActivity(new Intent(ProfileActivity.this, OrdersActivity.class));
        });
        binding.addressCount.setOnClickListener(v->{
            startActivity(new Intent(ProfileActivity.this, AddressActivity.class));
        });

        binding.logOut.setOnClickListener(v->{
            Helper.saveData(ProfileActivity.this, "user_id", null);
            Helper.saveData(ProfileActivity.this, "phone", null);
            Helper.saveData(ProfileActivity.this, "email", null);
            Toast.makeText(this, "Logout Successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });















    }

    @SuppressLint("ResourceAsColor")
    void updateData(String colName, TextView textView, String dataNow){
        DialogEditBinding dialogBinding = DialogEditBinding.inflate(LayoutInflater.from(ProfileActivity.this));

        AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this)
                .setView(dialogBinding.getRoot())
                .create();
        String userId = Helper.getData(ProfileActivity.this, "user_id");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if (dataNow != null){
            dialogBinding.editText.setText(dataNow);
        }

        dialogBinding.titlePlace.setText("Enter "+ Helper.capitalizeFirstLetter(colName));
        dialogBinding.loginBtn.setOnClickListener(v->{
            String des = dialogBinding.editText.getText().toString();
            if (des.isEmpty()){
                dialogBinding.editText.setError("*");
                return;
            }
            progressDialog.show();
            Call<ApiResponse> call = apiService.updateUserField(
                    userId,
                    colName,
                    des
            );
            Helper.hideKeyboard(ProfileActivity.this);
            updateUserData(call, textView, des);
            dialog.dismiss();
        });

        dialogBinding.closeBtn.setOnClickListener(v->{
            dialog.dismiss();
        });

        dialog.show();
    }

    void updateUserData(Call<ApiResponse> call, TextView textView, String des){
        progressDialog.show();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (textView != null) {
                        textView.setText(des);
                    }
                    Toast.makeText(ProfileActivity.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Helper.showOnlyMessage(ProfileActivity.this, "Error ",
                        "Something went wrong. " + t.getLocalizedMessage()+" Please try again later.");
                progressDialog.dismiss();
            }
        });
    }

    void getUserDetails(){
        String phone = Helper.getData(ProfileActivity.this, "phone");
        Call<SingleUserResponse> call = apiService.getUserDetails(phone);
        call.enqueue(new Callback<SingleUserResponse>() {
            @Override
            public void onResponse(Call<SingleUserResponse> call, Response<SingleUserResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    SingleUserResponse userResponse = response.body();
                    if (userResponse.getStatus().equalsIgnoreCase("success")){
                        UserModel userModel = userResponse.getData();

                        if (userModel.getImage() != null && !userModel.getImage().isEmpty()){
                            Glide.with(ProfileActivity.this)
                                    .load(userModel.getImage())
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .placeholder(getDrawable(R.drawable.placeholder))
                                    .into(binding.circleImageView);
                        }

                        if (userModel.getName() != null && !userModel.getName().isEmpty()){
                            binding.name.setText(userModel.getName());
                        }
                        if (userModel.getEmail() != null && !userModel.getEmail().isEmpty()){
                            binding.textView.setText(userModel.getEmail());
                        }
                        if (userModel.getPhone() != null && !userModel.getPhone().isEmpty()){
                            binding.phone.setText("+91 "+userModel.getPhone());
                        }
                        if (userModel.getGender() != null && !userModel.getGender().isEmpty()){
                            if (userModel.getGender().equalsIgnoreCase("male")){
                                binding.male.setChecked(true);
                                binding.female.setChecked(false);
                                binding.rNs.setChecked(false);
                            } else if (userModel.getGender().equalsIgnoreCase("female")) {
                                binding.male.setChecked(false);
                                binding.female.setChecked(true);
                                binding.rNs.setChecked(false);
                            }else {
                                binding.male.setChecked(false);
                                binding.female.setChecked(false);
                                binding.rNs.setChecked(true);
                            }
                        }

                        if (userModel.getState() != null && !userModel.getState().isEmpty()){
                            binding.state.setText(userModel.getState());
                        }

                        if (userModel.getDob() != null && !userModel.getDob().isEmpty()){
                            binding.dob.setText(userModel.getDob());
                        }

                        Helper.startCounter(userModel.getAddress_count(), binding.addressCount, "", "+");
                        Helper.startCounter(userModel.getOrder_count(), binding.ordersCount, "", "+");

                        binding.name.setOnClickListener(v->{
                            updateData("name", binding.name, userModel.getName());
                        });

                        binding.textView.setOnClickListener(v->{
                            updateData("email", binding.textView, userModel.getEmail());
                        });

                        binding.state.setOnClickListener(v->{
                            updateData("state", binding.state, userModel.getState());
                        });

                        binding.dob.setOnClickListener(v->{
                            int year = selectedCalendar.get(Calendar.YEAR);
                            int month = selectedCalendar.get(Calendar.MONTH);
                            int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);
                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    ProfileActivity.this,
                                    (view, selectedYear, selectedMonth, selectedDay) -> {
                                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                                        String formattedDate = sdf.format(selectedCalendar.getTime());

                                        // Display the selected date
                                        binding.dob.setText(formattedDate);
                                        Call<ApiResponse> call1 = apiService.updateUserField(
                                                userModel.getId(),
                                                "dob",
                                                formattedDate);
                                        updateUserData(call1, binding.dob, formattedDate);
                                    },
                                    year, month, day
                            );
                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                            datePickerDialog.show();
                        });

                        binding.genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                Call<ApiResponse> call;
                                if (checkedId == binding.male.getId()){
                                    call = apiService.updateUserField(
                                            userModel.getId(),
                                            "gender",
                                            "Male");
                                    updateUserData(call, null, null);
                                } else if (checkedId == binding.female.getId()) {
                                    call = apiService.updateUserField(
                                            userModel.getId(),
                                            "gender",
                                            "Female");
                                    updateUserData(call, null, null);
                                }else {
                                    call = apiService.updateUserField(
                                            userModel.getId(),
                                            "gender",
                                            "NA");
                                    updateUserData(call, null, null);
                                }

                            }
                        });

                    } else {
                        setDefData();
                    }
                }else{
                    setDefData();
                }
            }

            @Override
            public void onFailure(Call<SingleUserResponse> call, Throwable t) {
                setDefData();
            }
        });
    }

    void setDefData(){
        String email = Helper.getData(ProfileActivity.this, "email");
        String phone = Helper.getData(ProfileActivity.this, "phone");
        if (email != null){
            binding.textView.setText(email);
        }
        if (phone != null){
            binding.phone.setText("+91 " +phone);
        }
    }
}