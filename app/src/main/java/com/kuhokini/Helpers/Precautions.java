package com.kuhokini.Helpers;

import android.app.Activity;

import com.kuhokini.APIModels.VariantResponse;

public class Precautions {

    public static String EXPRESS_DELIVERY_DAYS = "Delivery within 4-7 days";
    public static String REGULAR_DELIVERY_DAYS = "Delivery within 7-10 days";
    public static int CASH_PAYMENT_CHARGES = 10;
    public static String COD_DES = "We're offering Cash on Delivery(COD) on this product. But it will cost extra <b>only ₹10/-</b> Also <b>delivery charges</b> will <b>increase</b> accordingly!";
    public static String RETURN_POLICY_DES = "We've 15days return policy on this product. But there are some terms and conditions for returning this product";
    public static int EXTRA_SAFETY_DELIVERY_CHARGES = 5;
    public static int EXPRESS_DELIVERY_CHARGES = 65;
    public static int SURFACE_DELIVERY_CHARGES = 45;
    public static int FREE_DELIVERY_AFTER = 1499;
    public static int WEIGHT = 500;
    public static boolean CASH_PAYMENT = false;
    public static boolean REGULAR_DELIVERY = true;

    public static String generateShareMessage(Activity activity, boolean value, VariantResponse.Variant productId, String name){
        if (value) {
            return "*" + name + "*\n" +
                    productId.getVarient_des()+"\n"+
                    "*Price:* ₹ " + productId.getSelling_price() + "\n" +
                    "🔗 *[View Details and Book Now]*(https://kuhokini.com/search_product.php?product-id=" + productId.getProduct_id()+")\n\n" +
                    "✨ *_This product is shared from Kuhokini App. For the best experience, check it out using our app!_* \n\n" +
                    "📲 *Download the Kuhokini App Now!* " +
                    "[Google Play Store](https://play.google.com/store/apps/details?id=" + activity.getPackageName() +")";
        } else {
            return  name + "\n" +productId.getVarient_name() + "\n" +
                    "Price: ₹ " + productId.getSelling_price() + "\n" +
                    "🔗 [View Details and Book Now]*(https://kuhokini.com/search_product.php?product-id=" + productId.getProduct_id()+")\n\n" +
                    "✨ This product is shared from Kuhokini App. For the best experience, check it out using our app! \n\n" +
                    "📲 Download the Kuhokini App Now!* " +
                    "[Google Play Store](https://play.google.com/store/apps/details?id=" + activity.getPackageName() +")";
        }
    }



}
