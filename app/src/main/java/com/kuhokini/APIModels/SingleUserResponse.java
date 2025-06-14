package com.kuhokini.APIModels;

public class SingleUserResponse {
    private String status;
    private String message;
    private UserModel data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public UserModel getData() {
        return data;
    }
}
