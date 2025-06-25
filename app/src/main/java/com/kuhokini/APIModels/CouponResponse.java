package com.kuhokini.APIModels;


import com.kuhokini.Models.CouponModel;

import java.util.List;

public class CouponResponse {
    private String status;
    private String message;
    private List<CouponModel> data;
    private CouponModel singleData;

    public CouponModel getSingleData() {
        return singleData;
    }

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

    public List<CouponModel> getData() {
        return data;
    }

    public void setData(List<CouponModel> data) {
        this.data = data;
    }
}
