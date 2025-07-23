package com.kuhokini.APIModels;

import com.kuhokini.Models.ReviewsModel;

import java.util.List;

public class ProductData {
    private int product_id;
    private String product_name;
    private String description;
    private int weight;
    private int featured;
    private int cat_id;
    private int sub_cat_id;
    private String delivery_charges;
    private ProductRatingData rating_info;
    private List<ReviewsModel> latest_reviews;
    private String tags;
    private List<VariantResponse.Variant> variants;

    // Getters and Setters


    public List<ReviewsModel> getLatest_reviews() {
        return latest_reviews;
    }

    public ProductRatingData getRating_info() {
        return rating_info;
    }

    public int getWeight() {
        return weight;
    }

    public int getProduct_id() {
        return product_id;
    }

    public boolean isFeatured(){
        if (featured == 0){
            return false;
        }else {
            return true;
        }
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public int getSub_cat_id() {
        return sub_cat_id;
    }

    public void setSub_cat_id(int sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public String getDelivery_charges() {
        return delivery_charges;
    }

    public void setDelivery_charges(String delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<VariantResponse.Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<VariantResponse.Variant> variants) {
        this.variants = variants;
    }
}
