package com.kuhokini.Account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener smsListener;

    // No-arg constructor required by Android
    public SmsReceiver() {
    }

    // Method to set the listener
    public static void setListener(SmsListener listener) {
        smsListener = listener;
    }

    // Clear listener when no longer needed
    public static void clearListener() {
        smsListener = null;
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        return filter;
    }

    public interface SmsListener {
        void onOtpReceived(String otp);
        void onOtpError(String error);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                if (status != null) {
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            if (message != null) {
                                Pattern pattern = Pattern.compile("(\\d{4,6})(?=\\s+is your OTP)");
                                Matcher matcher = pattern.matcher(message);
                                if (matcher.find()) {
                                    String otp = matcher.group(1).trim();
                                    if (smsListener != null) {
                                        smsListener.onOtpReceived(otp);
                                    }
                                }
                            }
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            if (smsListener != null) {
                                smsListener.onOtpError("OTP retrieval timed out");
                            }
                            break;
                        default:
                            if (smsListener != null) {
                                smsListener.onOtpError("Error retrieving OTP");
                            }
                            break;
                    }
                }
            }
        }
    }
}