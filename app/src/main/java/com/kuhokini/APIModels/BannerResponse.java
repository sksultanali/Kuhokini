package com.kuhokini.APIModels;

import com.developerali.Models.BannerModel;

import java.util.List;

public class BannerResponse {
    private String status;
    private String message;
    private List<BannerModel> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BannerModel> getData() {
        return data;
    }

    public void setData(List<BannerModel> data) {
        this.data = data;
    }
}
