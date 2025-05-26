package com.kuhokini.Models;

public class BannerModel {
    private String id;
    private String serial;
    private String imageUrl;
    private String message;
    private String link;
    private String btnTxt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }

    public String getBtnTxt() {
        return btnTxt;
    }
}
