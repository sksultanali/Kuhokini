package com.kuhokini.Account;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

public class SmsSender {

    public interface SmsCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public static String generateOTP() {
        Random rnd = new Random();
        int number = rnd.nextInt(9999);
        return String.format("%04d", number);
    }

    public static void sendSms(String phoneNumber, String message, String templateId, SmsCallback callback) {
        new Thread(() -> {
            try {
                // Encode Bengali/Unicode message
                //String encodedMessage = URLEncoder.encode(message, "UTF-8");
                Log.d("MESSAGE ENCODE", "This is a debug log for testing purposes. " +message);

                // Construct URL
                String urlString = "http://server1.bulksmsserver.in/smsserver/?" +
                        "UserID=kuhokini" +
                        "&UserPassWord=2025" +
                        "&PhoneNumber=" + phoneNumber +
                        "&Text=" + message +
                        "&GSM=KUHOKN" +
                        "&MsgFormat=1" +
                        "&DltPEID=1701174974038726318" +
                        "&DltTID="+templateId;

                // Open Connection
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                connection.disconnect();

                String response = responseBuilder.toString();
                String[] parts = response.split("\\|");

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (parts.length >= 3 && "Ok".equalsIgnoreCase(parts[0].trim())) {
                        String messageId = parts[2].trim();
                        callback.onSuccess(messageId);
                    } else if (parts.length >= 3 && "Error".equalsIgnoreCase(parts[0].trim())) {
                        String errorMsg = parts[2].trim();
                        callback.onError("Failed: " + errorMsg);
                    } else {
                        callback.onError("Unexpected response: " + response);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                // Error Callback
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
                }
            }
        }).start();
    }
}

