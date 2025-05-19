package com.kuhokini.Notification;

import android.os.AsyncTask;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public void getAccessToken(AccessTokenCallback callback) {
        new GetTokenTask(callback).execute();
    }

    private static class GetTokenTask extends AsyncTask<Void, Void, String> {
        private final AccessTokenCallback callback;

        GetTokenTask(AccessTokenCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String jsonString = "{\n" +
                        "  \"type\": \"service_account\",\n" +
                        "  \"project_id\": \"kuhokini-ca990\",\n" +
                        "  \"private_key_id\": \"9966659eca5892863994f18514100e73c49364da\",\n" +
                        "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDdsr7QXciWAy9i\\nUtvjNDZgR2enbl1rPqNGjObvfMzV2Fh+UxzMXyHn19PvH5/4iLWHF93R5yrCT2qR\\nU+d+LWH31vLRoQC7QhBPSDIkJDkXDmxxw/JGgQGH7f6Om2h/g2w2Co4V23W52PTi\\ncjBa02HbRX9h7mzzDnZS3SzJ8/R/iwPfk01awmDXQWJMLIUap3wS5BTSvb+S5G+t\\nGyQp+2imU4xF6MOepgmSYfLK40ta3v+ngftXtZwWBewpvA/k4O3cuhR22kSo1usy\\nVtd7orYy0Mp6SrJEFh6+XXTYMOGVErdaTIVMZtpqDh23NtzLNcwOsFUHFIF2ssXH\\nFYz9g2SrAgMBAAECggEAG/wXhhSF4MR0026DfyyfaIva6RGkt92SSAeP5yR+dB1O\\nc7O+FjY0AMtdCfmYpzYVOVZT6SPLC/gbtaM9EiUHEX06mCeWALfteHSJOnKwGi73\\nQBfB72W9cbvNvuadzEAksAr+YIKcBmJSlyW0Njgp9BR+aI2WMcreOMYz2dJHnLRi\\nuzfX36beRW5ujUwLZzfKqrSu+o7KziCr0wFkbPHCMlVStbq9j3zFInKeFFGlwTUm\\nds3k8cK8PDFDilG/XXU9/Nz3+xUbG1dRbWat1RsQyOslMAkTs5C5MXs+Iiu6L7Gl\\nILATrAW6HNJ2LRLeqPiLKEyrpsfxTcK7FdAl+yXGcQKBgQD3NsxZFpTjzgA5zwzu\\nD5CdBd5PGyi/1MuIu6gI0J3bTns1Wu4w/SwTdS8/SqYNh7ACatrLjcN7JGuJQQRw\\nfdTtEuL6SjjRefsmZjhKlTll2zTkG2MVafLiR4vTCg/GtX55CjkPCFbF8m+wc+xh\\nDhYFvBbqu3gy6vE113IYWXzF/QKBgQDlk8ySs37wVDk3T0PA8gcnl4MxaNf7+uPX\\nv6wwApbT1WpOnNP5nA+igOQK5xbTaJnCteK2aVg0/PxeDaBHcHoKgIL3g5gzphvC\\nq7oFx2rtJxwNI7qq7DJQYvWKXefIg9I7eeW7/iprp3FHFzvYGuRwNPA6gx5LhcBs\\nCxmxkBWBxwKBgF1MwybETQEE2DT5HaojGbMJafN1DpYHm6FJIYviIBGKtxlV7htz\\nEVVunpxGchEdKqJe7aBxKlupTSSJ80D5XIj03dEUfgkg/lZpsckENJtqdbW5WFXW\\nnKIl2nGVlIqsKMVJxGsV/JnMe7aeqZGfnVQgvo1sn1qoLMzjON5Tt7GJAoGADiNg\\nAdjUBHDrZSCYh5VgI2wNREm0o2v8BBllvT8rkrgd5+F4nHKj89oEMTeyDCrw7TDO\\nWXV7XSgHUrN8H2op2eO4LQ4JnYSpSP/SYPC7AQfbjmOY51XMc27kuDS4RXz4wXjs\\nUPuCGUcijsI7iT9+ufpIoRHaF8SQ6RZ16p149ZkCgYAr0WhFn0Xhb8GdNcJUac3t\\nsDz9T8U6ol3ljg+Sdx2dUVntwEx23xYUx9vjiBZPXn7K6IFm16ONgas5/3uHRiyU\\n7lrv0he7R91eX+sSqE1qDzbx5gA4k8+gnqpXVZTNias5rj3Mos/+MAF+ruw3tljP\\nrHoDXbH01NAsP62zWvXqUA==\\n-----END PRIVATE KEY-----\\n\",\n" +
                        "  \"client_email\": \"firebase-adminsdk-fbsvc@kuhokini-ca990.iam.gserviceaccount.com\",\n" +
                        "  \"client_id\": \"108761415452641663880\",\n" +
                        "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                        "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                        "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                        "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40kuhokini-ca990.iam.gserviceaccount.com\",\n" +
                        "  \"universe_domain\": \"googleapis.com\"\n" +
                        "}";
                InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
                GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                        .createScoped(firebaseMessagingScope);
                googleCredentials.refresh();
                return googleCredentials.getAccessToken().getTokenValue();
            } catch (Exception e) {
                Log.e("AccessToken", "getAccessToken: " + e.getLocalizedMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String token) {
            if (callback != null) {
                callback.onTokenReceived(token);
            }
        }
    }

    public interface AccessTokenCallback {
        void onTokenReceived(String token);
    }
}