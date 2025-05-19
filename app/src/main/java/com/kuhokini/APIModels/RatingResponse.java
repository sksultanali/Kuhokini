package com.kuhokini.APIModels;

public class RatingResponse {
    private String status;
    private String message;
    private ProductRatingData data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ProductRatingData getData() {
        return data;
    }
}
