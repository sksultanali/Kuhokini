package com.kuhokini.Models;

import java.util.List;

public class AddressResponse {
    private String status;
    private List<AddressModel> data;
    private int cod_enable;
    public String getStatus() {
        return status;
    }

    public int getCod_enable() {
        return cod_enable;
    }

    public List<AddressModel> getAddresses() {
        return data;
    }

    public class AddressModel {
        private String id;
        private String user_id;
        private String name;
        private String phone;
        private String state;
        private String pin;
        private String address;
        private String landmark;
        private int status;


        public String getId() {
            return id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getState() {
            return state;
        }

        public String getPin() {
            return pin;
        }

        public String getAddress() {
            return address;
        }

        public String getLandmark() {
            return landmark;
        }

        public int getStatus() {
            return status;
        }


    }


}
