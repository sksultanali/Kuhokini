package com.kuhokini.APIModels;

import com.kuhokini.Models.ReviewsModel;

import java.util.List;

public class ReviewResponse {
    private String status;
    private List<ReviewsModel> data;
    private int nextPageToken;
    private int count;

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ReviewsModel> getData() {
        return data;
    }

    public void setData(List<ReviewsModel> data) {
        this.data = data;
    }

    public int getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(int nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}


