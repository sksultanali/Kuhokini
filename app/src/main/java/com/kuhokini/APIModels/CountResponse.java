package com.kuhokini.APIModels;

public class CountResponse {
    private String status;
    private String message;
    private CountData data;

    // Getters and Setters
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

    public CountData getData() {
        return data;
    }
    public void setData(CountData data) {
        this.data = data;
    }

    public class CountData {
        private int categories;
        private int coupons;
        private int products;
        private int banners;

        // Getters and Setters
        public int getCategories() {
            return categories;
        }
        public void setCategories(int categories) {
            this.categories = categories;
        }

        public int getCoupons() {
            return coupons;
        }
        public void setCoupons(int coupons) {
            this.coupons = coupons;
        }

        public int getProducts() {
            return products;
        }
        public void setProducts(int products) {
            this.products = products;
        }

        public int getBanners() {
            return banners;
        }
        public void setBanners(int banners) {
            this.banners = banners;
        }
    }

}

