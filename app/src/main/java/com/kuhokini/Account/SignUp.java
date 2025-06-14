package com.kuhokini.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivitySignUpBinding;

import in.aabhasjindal.otptextview.OTPListener;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    ApiService apiService;
    ProgressDialog progressDialog;
    boolean passwordHide = true;
    String type;
    private CountDownTimer countDownTimer;

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
        if (type.equalsIgnoreCase("create")){
            binding.title.setText("Create New Account");
            binding.description.setText("We will send you an OTP to verify your Phone Number");
        }else {
            binding.title.setText("Forget Password?");
            binding.description.setText("Fill registered Phone Number and New Password");
        }

        progressDialog = new ProgressDialog(SignUp.this);
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

        binding.reqOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = binding.phoneEd.getText().toString();
                String password = binding.passwordBox.getText().toString();
                if (phone.isEmpty()){
                    binding.phoneEd.setError("*");
                }else if (password.isEmpty()){
                    binding.passwordBox.setError("*");
                }else {
                    progressDialog.setMessage("Checking server...");
                    progressDialog.show();
                    sendOtp();
                }
            }
        });

        binding.loginBtn.setOnClickListener(v->{
            Intent i = new Intent(SignUp.this, Login.class);
            startActivity(i);
            finish();
        });

        binding.psValid.setText(getPasswordRequirements());
        binding.passwordBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0){
                    binding.passwordValidationText.setVisibility(View.GONE);
                    binding.psValid.setVisibility(View.GONE);
                }else {
                    binding.passwordValidationText.setVisibility(View.VISIBLE);
                    binding.psValid.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword(s.toString());
            }
        });

        binding.reSend.setOnClickListener(v->{
            sendOtp();
        });



    }

    private void sendOtp() {
        String phone = binding.phoneEd.getText().toString();
        String otpG = SmsSender.generateOTP();
        SmsSender.sendSms(phone, getOtpMessage(otpG), "1707174979376480801", new SmsSender.SmsCallback() {
            @Override
            public void onSuccess(String response) {
                binding.optSec.setVisibility(View.VISIBLE);
                binding.reSend.setTextColor(getColor(R.color.icon_color));
                binding.reSend.setEnabled(false);
                startCountdownTimer();
                progressDialog.dismiss();

                binding.otpView.setOtpListener(new OTPListener() {
                    @Override
                    public void onInteractionListener() {
                        // fired when user types something in the OtpBox
                    }
                    @Override
                    public void onOTPComplete(String otp) {
                        if (otp.equalsIgnoreCase(otpG)){
                            Toast.makeText(SignUp.this, "perfect", Toast.LENGTH_SHORT).show();
                        }else {
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

    public String getOtpMessage(String otp){
        if (type.equalsIgnoreCase("create")){
           return "Hello dear, "+ otp +" is your OTP from KUHOKINI for account creation. For security reasons, DO NOT share the OTP with anyone.\nThank You";
        }else {
            return "Hello dear, "+ otp +" is your OTP from KUHOKINI for account creation. For security reasons, DO NOT share the OTP with anyone.\nThank You";
        }
    }

    private void startCountdownTimer() {
        // Cancel previous timer if exists
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
    }

    private void validatePassword(String password) {
        if (password.isEmpty()) {
            binding.passwordValidationText.setText("");
            binding.passwordValidationText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            return;
        }

        boolean isValid = isValidPassword(password);

        if (isValid) {
            binding.passwordValidationText.setText("Password strength: Strong");
            binding.passwordValidationText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            binding.passwordValidationText.setText("Password doesn't meet requirements");
            binding.passwordValidationText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // Check for at least 8 characters
        if (password.length() < 8) {
            return false;
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }

        return true;
    }

    public static String getPasswordRequirements() {
        return "Password must contain:\n" +
                "- Minimum 8 characters\n" +
                "- At least one uppercase letter\n" +
                "- At least one lowercase letter\n" +
                "- At least one number\n" +
                "- At least one special character";
    }

}