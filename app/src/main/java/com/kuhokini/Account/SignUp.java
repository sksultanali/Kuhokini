package com.kuhokini.Account;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kuhokini.APIModels.SingleUserResponse;
import com.kuhokini.Helpers.ApiResponse;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.MainActivity;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivitySignUpBinding;

import in.aabhasjindal.otptextview.OTPListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    ApiService apiService;
    ProgressDialog progressDialog;
    boolean passwordHide = true;
    String type, generatedOtp;
    private CountDownTimer countDownTimer;
    private static final int SMS_PERMISSION_REQUEST_CODE = 100;
    private SmsReceiver smsReceiver;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());

        type = getIntent().getStringExtra("type");
        if (type.equalsIgnoreCase("create")) {
            binding.title.setText("Create New Account");
            binding.description.setText("We will send you an OTP to verify your Phone Number");
        } else {
            binding.title.setText("Forget Password");
            binding.description.setText("Fill registered Phone Number and New Password");
        }

        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setCancelable(false);

        binding.eyeBtn.setOnClickListener(v -> {
            if (passwordHide) {
                binding.passwordBox.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.eyeBtn.setImageDrawable(getDrawable(R.drawable.password_icon));
                passwordHide = false;
            } else {
                binding.passwordBox.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.eyeBtn.setImageDrawable(getDrawable(R.drawable.password_off));
                passwordHide = true;
            }
        });

        binding.reqOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = binding.phoneEd.getText().toString();
                String password = binding.passwordBox.getText().toString();
                if (phone.isEmpty()) {
                    binding.phoneEd.setError("*");
                } else if (password.isEmpty()) {
                    binding.passwordBox.setError("*");
                } else if (!isValidPassword(password)) {
                    Helper.showOnlyMessage(SignUp.this,"InEligible Password!", "Password does not meet requirements\n" +
                            "\"-Minimum 8 characters\",\n" +
                            "\"-At least one uppercase letter\",\n" +
                            "\"-At least one lowercase letter\",\n" +
                            "\"-At least one number\",\n" +
                            "\"-At least one special character\"");
                } else {
                    Helper.hideKeyboard(SignUp.this);
                    progressDialog.setMessage("Checking server...");
                    progressDialog.show();
                    binding.reqOtp.setVisibility(View.GONE);
                    binding.loginBtn.setVisibility(View.GONE);
                    Call<SingleUserResponse> call = apiService.getUserDetails(phone);
                    call.enqueue(new Callback<SingleUserResponse>() {
                        @Override
                        public void onResponse(Call<SingleUserResponse> call, Response<SingleUserResponse> response) {
                            if (response.isSuccessful() && response.body() != null){
                                SingleUserResponse userResponse = response.body();
                                if (userResponse.getStatus().equalsIgnoreCase("success")){
                                    Helper.showActionDialog(SignUp.this, "Already Registered",
                                            "<b>+91 " + phone + "</b> is already registered with us! Please login or try any other mobile no to register !",
                                            "Login", "Use Another Number", true, new Helper.DialogButtonClickListener() {
                                                @Override
                                                public void onYesButtonClicked() {
                                                    startActivity(new Intent(SignUp.this, Login.class));
                                                    finish();
                                                }

                                                @Override
                                                public void onNoButtonClicked() {
                                                    resetFields();
                                                }

                                                @Override
                                                public void onCloseButtonClicked() {
                                                    binding.reqOtp.setVisibility(View.VISIBLE);
                                                    binding.loginBtn.setVisibility(View.VISIBLE);
                                                }
                                            });
                                    progressDialog.dismiss();
                                }else {
                                    sendOtp();
                                }
                            }else {
                                sendOtp();
                            }

                            //Log.d("API_URL", call.request().url().toString());
                        }

                        @Override
                        public void onFailure(Call<SingleUserResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Helper.showOnlyMessage(SignUp.this, "Error!", "Something went wrong. "+
                                    t.getLocalizedMessage());
                        }
                    });
                }
            }
        });

        binding.loginBtn.setOnClickListener(v -> {
            Intent i = new Intent(SignUp.this, Login.class);
            startActivity(i);
            finish();
        });

        // Initialize password validation text
        updatePasswordValidationText("");
        binding.passwordBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    binding.passwordValidationText.setVisibility(View.GONE);
                    binding.psValid.setVisibility(View.GONE);
                } else {
                    binding.passwordValidationText.setVisibility(View.VISIBLE);
                    binding.psValid.setVisibility(View.GONE); // Hide the separate requirements TextView
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePasswordValidationText(s.toString());
            }
        });

        binding.reSend.setOnClickListener(v -> {
            sendOtp();
        });

        // Start SMS Retriever
        checkAndRequestSmsPermission();
        registerForToken();

    }

    void resetFields(){
        binding.passwordBox.setText("");
        binding.phoneEd.setText("");
        binding.reqOtp.setVisibility(View.VISIBLE);
        binding.loginBtn.setVisibility(View.VISIBLE);
    }

    void registerForToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        token = task.getResult();
                    }
                });
    }

    void registerData(){
        String phone = binding.phoneEd.getText().toString();
        String password = binding.passwordBox.getText().toString();
        progressDialog.setMessage("Creating new account...");
        progressDialog.show();
        Call<ApiResponse> call = apiService.insertUser(password, phone, null, token);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getStatus().equalsIgnoreCase("success")){
                        Helper.saveData(SignUp.this, "user_id", apiResponse.getUser_id());
                        Helper.saveData(SignUp.this, "phone", phone);
                        //Helper.saveData(SignUp.this, "email", apiResponse.getData().getEmail());
                        //startActivity(new Intent(SignUp.this, MainActivity.class));
                        finish();
                    }else {
                        Helper.showOnlyMessage(SignUp.this, apiResponse.getStatus(),
                                apiResponse.getMessage());
                    }
                    progressDialog.dismiss();
                }else{
                    Helper.showOnlyMessage(SignUp.this, "Error Caught", "Something went wrong! "
                            + ", Please try again.");
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                Helper.showOnlyMessage(SignUp.this, "Error Caught", "Something went wrong! "+
                        t.getLocalizedMessage() + ", Please try again.");
            }
        });
    }

    private void checkAndRequestSmsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_SMS
                        },
                        SMS_PERMISSION_REQUEST_CODE);
            } else {
                startSmsRetriever();
            }
        } else {
            startSmsRetriever();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSmsRetriever();
            } else {
                Toast.makeText(this, "SMS permission is required for automatic OTP verification",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startSmsRetriever() {
        try {
            SmsRetrieverClient client = SmsRetriever.getClient(this);
            Task<Void> task = client.startSmsRetriever();

            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Successfully started retriever
                    registerSmsReceiver();
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUp.this,
                            "Failed to start SMS Retriever: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
//            Toast.makeText(this, "Error initializing SMS Retriever: " + e.getMessage(),
//                    Toast.LENGTH_SHORT).show();
        }
    }

    private void registerSmsReceiver() {
        // Set the listener before registering
        SmsReceiver.setListener(new SmsReceiver.SmsListener() {
            @Override
            public void onOtpReceived(String otp) {
                runOnUiThread(() -> {
                    Log.d("OTP_DEBUG", "Received OTP: " + otp);
                    if (binding != null && binding.otpView != null) {
                        binding.otpView.setOTP(otp);
                        verifyOtp(otp);
                    }
                });
            }

            @Override
            public void onOtpError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(SignUp.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });

        // Register the receiver
        try {
            registerReceiver(new SmsReceiver(), SmsReceiver.getIntentFilter(),
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                            Context.RECEIVER_NOT_EXPORTED : 0);
            Log.d("OTP_DEBUG", "SMS Receiver registered successfully");
        } catch (Exception e) {
            Log.e("OTP_DEBUG", "Failed to register receiver: " + e.getMessage());
        }
    }


    private void sendOtp() {
        String phone = binding.phoneEd.getText().toString();
        generatedOtp = SmsSender.generateOTP();
        SmsSender.sendSms(phone, getOtpMessage(generatedOtp), "1707174979376480801", new SmsSender.SmsCallback() {
            @Override
            public void onSuccess(String response) {
                binding.optSec.setVisibility(View.VISIBLE);
                binding.reSend.setTextColor(getColor(R.color.icon_color));
                binding.reSend.setEnabled(false);
                startCountdownTimer();
                progressDialog.dismiss();

                //binding.otpView.setOTP("1234");
                binding.otpView.setOtpListener(new OTPListener() {
                    @Override
                    public void onInteractionListener() {
                        // Fired when user types something in the OtpBox
                    }

                    @Override
                    public void onOTPComplete(String otp) {
                        verifyOtp(otp);
                    }
                });
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Helper.showActionDialog(SignUp.this, "Failed",
                        "Something went wrong please try after sometime...! " + error,
                        null, "Try Again", true, new Helper.DialogButtonClickListener() {
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

    private void verifyOtp(String otp) {
        if (otp.equalsIgnoreCase(generatedOtp)) {
            registerData();
        } else {
            Helper.showActionDialog(SignUp.this, "OTP Mismatch",
                    "The verification code you entered is incorrect. Please try again...!",
                    null, "Try Again", true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {}
                        @Override
                        public void onNoButtonClicked() {}
                        @Override
                        public void onCloseButtonClicked() {}
                    });
        }
    }

    public String getOtpMessage(String otp) {
        // Ensure the SMS starts with <#> for SMS Retriever API
        if (type.equalsIgnoreCase("create")) {
            return "Hello dear, " + otp + " is your OTP from KUHOKINI for account creation. For security reasons, DO NOT share the OTP with anyone.\nThank You";
        } else {
            return "Hello dear, " + otp + " is your OTP from KUHOKINI for password reset. For security reasons, DO NOT share the OTP with anyone.\nThank You";
        }
    }

    private void startCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                binding.timerCount.setText("OTP request in " + secondsRemaining + " sec");
            }

            public void onFinish() {
                binding.timerCount.setText("OTP expired");
                binding.reSend.setEnabled(true);
                binding.reSend.setTextColor(getColor(R.color.blue_purple));
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        try {
            if (smsReceiver != null) {
                unregisterReceiver(smsReceiver);
            }
        } catch (Exception e) {
            // Receiver was not registered
        }
    }

    private void updatePasswordValidationText(String password) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        // Define criteria and their validation status
        String[] criteria = {
                "Minimum 8 characters",
                "At least one uppercase letter",
                "At least one lowercase letter",
                "At least one number",
                "At least one special character"
        };
        boolean[] isMet = {
                password.length() >= 8,
                password.matches(".*[A-Z].*"),
                password.matches(".*[a-z].*"),
                password.matches(".*\\d.*"),
                password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")
        };

        // Check if all criteria are met
        boolean allCriteriaMet = true;
        for (boolean met : isMet) {
            if (!met) {
                allCriteriaMet = false;
                break;
            }
        }

        if (allCriteriaMet) {
            // Display success message
            String successMessage = "Woh! Good to go 👍🏻";
            builder.append(successMessage);
            builder.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.holo_green_dark)),
                    0,
                    builder.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        } else {
            // Build the spannable text for criteria
            for (int i = 0; i < criteria.length; i++) {
                String line = criteria[i] + "\n";
                builder.append(line);
                int start = builder.length() - line.length();
                int end = builder.length();
                int color = isMet[i] ? ContextCompat.getColor(this, android.R.color.holo_green_dark)
                        : ContextCompat.getColor(this, android.R.color.holo_red_dark);
                builder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        binding.passwordValidationText.setText(builder);
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }
        return true;
    }

}