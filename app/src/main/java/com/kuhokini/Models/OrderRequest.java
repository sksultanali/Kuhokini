package com.kuhokini.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class OrderRequest implements Parcelable{
    String user_id, payment_type, address_id;
    int payment_amount;
    List<ProductDetailsModel> product_details;
    List<PriceBreakdown> price_breakdown;
    String shipment_type, exp_arrival;

    public OrderRequest(String user_id, String payment_type, String address_id, int payment_amount,
                        List<ProductDetailsModel> product_details, List<PriceBreakdown> price_breakdown,
                        String shipment_type, String exp_arrival) {
        this.user_id = user_id;
        this.address_id = address_id;
        this.payment_type = payment_type;
        this.payment_amount = payment_amount;
        this.product_details = product_details;
        this.price_breakdown = price_breakdown;
        this.shipment_type = shipment_type;
        this.exp_arrival = exp_arrival;
    }

    protected OrderRequest(Parcel in) {
        user_id = in.readString();
        payment_type = in.readString();
        payment_amount = in.readInt();
        product_details = in.createTypedArrayList(ProductDetailsModel.CREATOR);
        shipment_type = in.readString();
        exp_arrival = in.readString();
    }

    public static final Creator<OrderRequest> CREATOR = new Creator<OrderRequest>() {
        @Override
        public OrderRequest createFromParcel(Parcel in) {
            return new OrderRequest(in);
        }

        @Override
        public OrderRequest[] newArray(int size) {
            return new OrderRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(payment_type);
        parcel.writeInt(payment_amount);
        parcel.writeTypedList(product_details);
        parcel.writeString(shipment_type);
        parcel.writeString(exp_arrival);
    }

    public static class ProductDetailsModel implements Parcelable {
        int product_id;
        String variant_id, name;
        int selling_price, qty;

        public ProductDetailsModel(int product_id, String variant_id, String name, int selling_price, int qty) {
            this.product_id = product_id;
            this.variant_id = variant_id;
            this.name = name;
            this.selling_price = selling_price;
            this.qty = qty;
        }

        protected ProductDetailsModel(Parcel in) {
            product_id = in.readInt();
            variant_id = in.readString();
            name = in.readString();
            selling_price = in.readInt();
            qty = in.readInt();
        }

        public static final Creator<ProductDetailsModel> CREATOR = new Creator<ProductDetailsModel>() {
            @Override
            public ProductDetailsModel createFromParcel(Parcel in) {
                return new ProductDetailsModel(in);
            }

            @Override
            public ProductDetailsModel[] newArray(int size) {
                return new ProductDetailsModel[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeInt(product_id);
            parcel.writeString(variant_id);
            parcel.writeString(name);
            parcel.writeInt(selling_price);
            parcel.writeInt(qty);
        }
    }

    public class PriceBreakdown {
        String title;
        int amount;

        public PriceBreakdown(String title, int amount) {
            this.title = title;
            this.amount = amount;
        }
    }

}
