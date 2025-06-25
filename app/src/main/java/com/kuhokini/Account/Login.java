package com.kuhokini.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kuhokini.APIModels.SingleUserResponse;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.MainActivity;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    ApiService apiService;
    ProgressDialog progressDialog;
    boolean passwordHide = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setCancelable(false);

        binding.eyeBtn.setOnClickListener(v->{
            if (passwordHide){
                binding.passwordBox.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.eyeBtn.setImageDrawable(getDrawable(R.drawable.password_icon));
                passwordHide = false;
            }else {
                binding.passwordBox.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.eyeBtn.setImageDrawable(getDrawable(R.drawable.password_off));
                passwordHide = true;
            }
        });

        binding.loginBtn.setOnClickListener(v->{
            String phone = binding.phoneEd.getText().toString();
            String password = binding.passwordBox.getText().toString();
            if (phone.isEmpty()){
                binding.phoneEd.setError("*");
            }else if (password.isEmpty()){
                binding.passwordBox.setError("*");
            }else {
                Call<SingleUserResponse> call = apiService.getUserDetails(phone);
                Helper.hideKeyboard(Login.this);
                progressDialog.setMessage("Finding profile details...");
                progressDialog.show();
                call.enqueue(new Callback<SingleUserResponse>() {
                    @Override
                    public void onResponse(Call<SingleUserResponse> call, Response<SingleUserResponse> response) {
                        if (response.isSuccessful() && response.body() != null){
                            SingleUserResponse userResponse = response.body();
                            if (userResponse.getStatus().equalsIgnoreCase("success")){
                                if (userResponse.getData().getPassword().equalsIgnoreCase(password)){
                                    Helper.saveData(Login.this, "user_id", userResponse.getData().getId());
                                    Helper.saveData(Login.this, "phone", userResponse.getData().getPhone());
                                    Helper.saveData(Login.this, "email", userResponse.getData().getEmail());
                                    //startActivity(new Intent(Login.this, MainActivity.class));
                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    finish();
                                }else {
                                    Helper.showActionDialog(Login.this, "Password Mismatch",
                                            "Your phone number and your password is not matching...!",
                                            "Forget Password", "Try Again", true, new Helper.DialogButtonClickListener() {
                                                @Override
                                                public void onYesButtonClicked() {
                                                    binding.forgetPw.performClick();
                                                }
                                                @Override
                                                public void onNoButtonClicked() {}
                                                @Override
                                                public void onCloseButtonClicked() {}
                                            });
                                }
                            } else if (userResponse.getStatus().equalsIgnoreCase("not_found")) {
                                Helper.showActionDialog(Login.this, userResponse.getStatus(),
                                        userResponse.getMessage(),
                                        "Create New Account", "Try Again", true, new Helper.DialogButtonClickListener() {
                                            @Override
                                            public void onYesButtonClicked() {
                                                binding.createAccount.performClick();
                                            }
                                            @Override
                                            public void onNoButtonClicked() {}
                                            @Override
                                            public void onCloseButtonClicked() {}
                                        });
                            } else {
                                Helper.showActionDialog(Login.this, userResponse.getStatus(),
                                        userResponse.getMessage(),
                                        "Okay", null, true, new Helper.DialogButtonClickListener() {
                                            @Override
                                            public void onYesButtonClicked() {}
                                            @Override
                                            public void onNoButtonClicked() {}
                                            @Override
                                            public void onCloseButtonClicked() {}
                                        });
                            }
                            progressDialog.dismiss();
                        }else{
                            progressDialog.dismiss();
                            Helper.showOnlyMessage(Login.this, "Error!", "Something went wrong. " +
                                    "Please check entered phone number and try again!");
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleUserResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Helper.showActionDialog(Login.this, "Error 405",
                                "Something went wrong, please try again" + t.getLocalizedMessage(),
                                "Okay", null, true, new Helper.DialogButtonClickListener() {
                                    @Override
                                    public void onYesButtonClicked() {}
                                    @Override
                                    public void onNoButtonClicked() {}
                                    @Override
                                    public void onCloseButtonClicked() {}
                                });
                    }
                });
            }
        });

        binding.createAccount.setOnClickListener(v->{
            Intent i = new Intent(Login.this, SignUp.class);
            i.putExtra("type", "create");
            startActivity(i);
            finish();
        });

        binding.forgetPw.setOnClickListener(v->{
            Intent i = new Intent(Login.this, SignUp.class);
            i.putExtra("type", "forget");
            startActivity(i);
            finish();
        });




    }
}