package com.kuhokini.Models;

public class OrderModel {

    String name, status, amount, productName, img;

    public OrderModel() {
    }

    public OrderModel(String productName,String name, String status,
                      String amount, String img) {
        this.productName = productName;
        this.name = name;
        this.status = status;
        this.amount = amount;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
