package com.kuhokini.APIModels;

import java.util.List;

public class SearchKeywordResponse {

    private String status;
    private String message;
    private List<Data> data;

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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data{
        private String product_name;
        private String variant_name;

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getVariant_name() {
            return variant_name;
        }

        public void setVariant_name(String variant_name) {
            this.variant_name = variant_name;
        }
    }

}
