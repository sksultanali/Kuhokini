package com.kuhokini.APIModels;

import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class RoomImagesResponse {
    private String status;
    private String message;
    ArrayList<SlideModel> images;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<SlideModel> getImages() {
        return images;
    }
}
