package com.kuhokini.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;


import com.kuhokini.APIModels.ImageUploadResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUploader {
    private Context context;
    private ApiService apiService;
    private String folderName;
    private UploadListener uploadListener;

    public interface UploadListener {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(String errorMessage);
        void onError(Exception e);
    }

    public ImageUploader(Context context, String folderName, UploadListener listener) {
        this.context = context;
        this.folderName = folderName;
        this.uploadListener = listener;
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void uploadImage(Uri imageUri) {
        try {
            File file = uriToFile(imageUri);
            if (file != null) {
                uploadImageFile(file);
            } else {
                String error = "Failed to get file from URI";
                Log.e("ImageUploader", error);
                if (uploadListener != null) {
                    uploadListener.onUploadFailure(error);
                }
            }
        } catch (Exception e) {
            Log.e("ImageUploader", "Error in uploadImage", e);
            if (uploadListener != null) {
                uploadListener.onError(e);
            }
        }
    }

    private File uriToFile(Uri uri) throws IOException {
        File file = null;
        if ("content".equals(uri.getScheme())) {
            try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                String fileName = getFileName(uri);
                File cacheFile = new File(context.getCacheDir(), fileName);
                try (OutputStream outputStream = new FileOutputStream(cacheFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
                file = cacheFile;
            }
        } else if ("file".equals(uri.getScheme())) {
            file = new File(uri.getPath());
        }
        return file;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        return fileName;
    }

    private void uploadImageFile(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        Call<ImageUploadResponse> call = apiService.uploadImage(folderName, body);
        call.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (!response.isSuccessful()) {
                    String errorMsg = "Server error: " + response.code();
                    try {
                        errorMsg += " - " + response.errorBody().string();
                    } catch (IOException e) {
                        Log.e("ImageUploader", "Error reading error body", e);
                    }
                    handleUploadFailure(errorMsg);
                    return;
                }

                ImageUploadResponse imageResponse = response.body();
                if (imageResponse != null && "success".equalsIgnoreCase(imageResponse.getStatus())) {
                    handleUploadSuccess(imageResponse.getData().getUrl());
                } else {
                    handleUploadFailure(imageResponse != null ?
                            imageResponse.getMessage() : "Unknown error");
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                handleUploadFailure("Network error: " + t.getMessage());
            }
        });
    }

    private void handleUploadSuccess(String imageUrl) {
        Log.d("ImageUploader", "Upload successful: " + imageUrl);
        if (uploadListener != null) {
            uploadListener.onUploadSuccess(imageUrl);
        }
    }

    private void handleUploadFailure(String error) {
        Log.e("ImageUploader", error);
        if (uploadListener != null) {
            uploadListener.onUploadFailure(error);
        }
    }
}
