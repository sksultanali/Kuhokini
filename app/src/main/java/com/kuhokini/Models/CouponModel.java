package com.kuhokini.Models;

public class CouponModel {
    private String id;
    private String code;
    private String type;
    private String valid;
    private int percentageOrAmount;
    private String product_id;
    private String product_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public int getPercentageOrAmount() {
        return percentageOrAmount;
    }

    public void setPercentageOrAmount(int percentageOrAmount) {
        this.percentageOrAmount = percentageOrAmount;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}
