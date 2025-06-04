package com.kuhokini.APIModels;
import com.denzcoskun.imageslider.models.SlideModel;
import com.hishd.tinycart.model.Item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VariantResponse {
    private String status;
    private int product_id;
    private List<Variant> variants;

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    public class Variant implements Item, Serializable {
        private String id;
        private int product_id;
        private String varient_name;
        private int stock;
        private String varient_des;
        private int selling_price;
        private int normal_price;
        private int quantity;
        ArrayList<SlideModel> images; // You can use List<String> if images is a comma-separated string of URLs

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public String getVarient_name() {
            return varient_name;
        }

        public void setVarient_name(String varient_name) {
            this.varient_name = varient_name;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public String getVarient_des() {
            return varient_des;
        }

        public void setVarient_des(String varient_des) {
            this.varient_des = varient_des;
        }

        public int getSelling_price() {
            return selling_price;
        }

        public void setSelling_price(int selling_price) {
            this.selling_price = selling_price;
        }

        public int getNormal_price() {
            return normal_price;
        }

        public void setNormal_price(int normal_price) {
            this.normal_price = normal_price;
        }

        public void setImages(ArrayList<SlideModel> images) {
            this.images = images;
        }

        public ArrayList<SlideModel> getImages() {
            return images;
        }

        @Override
        public BigDecimal getItemPrice() {
            return new BigDecimal(selling_price);
        }

        @Override
        public String getItemName() {
            return id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

}

