package com.kuhokini.Helpers;

import com.kuhokini.DeliveryModels.InvoiceResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface DelhiveryApiService {
    @GET("api/kinko/v1/invoice/charges/.json")
    Call<List<InvoiceResponse>> getInvoiceCharges(
            @Header("Authorization") String authToken,
            @Query("md") String md,
            @Query("ss") String ss,
            @Query("d_pin") String deliveryPin,
            @Query("o_pin") String originPin,
            @Query("cgm") int weight,
            @Query("pt") String paymentType
    );
}
