package com.kuhokini.Models;

public class CategoryModel {

    String id, name, image, subcategory_count, product_count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubcategory_count() {
        return subcategory_count;
    }

    public void setSubcategory_count(String subcategory_count) {
        this.subcategory_count = subcategory_count;
    }

    public String getProduct_count() {
        return product_count;
    }

    public void setProduct_count(String product_count) {
        this.product_count = product_count;
    }

    @Override
    public String toString() {
        return name;
    }
}
