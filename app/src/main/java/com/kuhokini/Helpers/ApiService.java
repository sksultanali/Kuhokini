package com.kuhokini.Helpers;
import androidx.annotation.Nullable;

import com.kuhokini.APIModels.BannerResponse;
import com.kuhokini.APIModels.CategoryResponse;
import com.kuhokini.APIModels.CountResponse;
import com.kuhokini.APIModels.CouponResponse;
import com.kuhokini.APIModels.ImageUploadResponse;
import com.kuhokini.APIModels.MainResponse;
import com.kuhokini.APIModels.ProductResponse;
import com.kuhokini.APIModels.RoomImagesResponse;
import com.kuhokini.APIModels.SearchKeywordResponse;
import com.kuhokini.APIModels.SingleUserResponse;
import com.kuhokini.APIModels.TagsResponse;
import com.kuhokini.APIModels.UserResponse;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.Models.AddressResponse;
import com.kuhokini.Notification.NotificationRequest;
import com.kuhokini.Notification.NotificationRequestToken;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @POST("v1/projects/kuhokini-ca990/messages:send")
    Call<Void> sendNotification(
            @Header("Authorization") String authorization,
            @Body NotificationRequest notificationRequest
    );

    @POST("v1/projects/kuhokini-ca990/messages:send")
    Call<Void> sendNotificationToken(
            @Header("Authorization") String authorization,
            @Body NotificationRequestToken notificationRequestToken
    );






    //delete and edit any table;
    @GET("apis/api.php?action=editTable")
    Call<ApiResponse> editTable(
            @Query("tableName") String tableName,
            @Query("fieldName") String fieldName,
            @Query("fieldValue") String fieldValue,
            @Query("id") String id
    );

    @GET("apis/api.php?action=deleteTable")
    Call<ApiResponse> deleteTable(
            @Query("tableName") String tableName,
            @Query("id") String id
    );

    @GET("apis/api.php?action=updateProductImage")
    Call<ApiResponse> updateProductImage(
            @Query("id") String id,
            @Query("imageUrl") String imageUrl
    );

    @GET("apis/api.php?action=deleteProductImage")
    Call<ApiResponse> deleteProductImage(
            @Query("id") String id,
            @Query("imageUrl") String imageUrl
    );

    @GET("apis/api.php?action=updateUserField")
    Call<ApiResponse> updateUserField(
            @Query("user_id") String user_id,
            @Query("field") String field,
            @Query("value") String value
    );







    // Insert APIs
    @GET("apis/api.php?action=addAddress")
    Call<ApiResponse> insertAddress(
            @Query("name") String name,
            @Query("user_id") String user_id,
            @Query("phone") String phone,
            @Query("state") String state,
            @Query("pin") String pin,
            @Query("address") String address,
            @Query("landmark") @Nullable String landmark
    );

    @GET("apis/api.php?action=insertCategory")
    Call<ApiResponse> insertCategory(
            @Query("name") String name,
            @Query("image") String image
    );

    @POST("apis/post_api.php?action=insertUser")
    Call<ApiResponse> insertUser(
            @Query("password") String password,
            @Query("phone") String phone,
            @Query("email") String email,
            @Query("token") String token
    );

    @GET("apis/api.php?action=insertSubCategory")
    Call<ApiResponse> insertSubCategory(
            @Query("name") String name,
            @Query("image") String image,
            @Query("parent_cat") String parent_cat
    );

    @GET("apis/api.php?action=addBanner")
    Call<ApiResponse> addBanner(
            @Query("serial") String serial,
            @Query("imageUrl") String imageUrl
    );

    @GET("apis/api.php?action=updateAddressStatus")
    Call<ApiResponse> updateAddressStatus(
            @Query("user_id") String user_id,
            @Query("address_id") String address_id
    );

    @GET("apis/api.php?action=addProduct")
    Call<ApiResponse> addProduct(
            @Query("name") String name,
            @Query("description")@Nullable String description,
            @Query("cat_id") String cat_id,
            @Query("sub_cat_id")@Nullable String sub_cat_id,
            @Query("delivery_charges")@Nullable String delivery_charges,
            @Query("weight")@Nullable String weight
    );

    @GET("apis/api.php?action=updateProduct")
    Call<ApiResponse> updateProduct(
            @Query("id") String id,
            @Query("name") String name,
            @Query("description")@Nullable String description,
            @Query("cat_id") String cat_id,
            @Query("sub_cat_id")@Nullable String sub_cat_id,
            @Query("weight")@Nullable String weight
    );

    @GET("apis/api.php?action=addVariant")
    Call<ApiResponse> addVariant(
            @Query("product_id") String product_id,
            @Query("varient_name") String varient_name,
            @Query("varient_des") String varient_des,
            @Query("stock") String stock,
            @Query("selling_price") String selling_price,
            @Query("normal_price") String normal_price,
            @Query("images")@Nullable String images
    );

    @GET("apis/api.php?action=updateVariant")
    Call<ApiResponse> updateVariant(
            @Query("variant_id") String variant_id,
            @Query("product_id") String product_id,
            @Query("varient_name") String varient_name,
            @Query("varient_des") String varient_des,
            @Query("stock") String stock,
            @Query("selling_price") String selling_price,
            @Query("normal_price") String normal_price
    );

    @Multipart
    @POST("apis/api.php?action=uploadImage") // Replace with your endpoint
    Call<ImageUploadResponse> uploadImage(
            @Query("folder_location") String folder_location,
            @Part MultipartBody.Part image
    );

    @GET("apis/api.php?action=addCoupon")
    Call<ApiResponse> addCoupon(
            @Query("homeId")@Nullable String homeId,
            @Query("code") String code,
            @Query("type") String type,
            @Query("valid") int valid,
            @Query("percentageOrAmount") String percentageOrAmount
    );











    // Fetch or Other APIs
    @GET("apis/api.php?action=getCoupons")
    Call<CouponResponse> getCoupons(
            @Query("productId")@Nullable String productId,
            @Query("keyword")@Nullable String keyword
    );

    @GET("testApis/api.php?action=checkCouponCode")
    Call<CouponResponse> checkCouponCode(
            @Query("code") String code
    );

    @GET("apis/api.php?action=getProductDetails")
    Call<ProductResponse> getProductDetails(
            @Query("id") String id
    );

    @GET("apis/api.php?action=fetchAddresses")
    Call<AddressResponse> getAddresses(
            @Query("userId") String userId
    );

    @GET("apis/api.php?action=getUserDetails")
    Call<SingleUserResponse> getUserDetails(
            @Query("phone") String phone
    );

    @GET("apis/api.php?action=getTagsById")
    Call<TagsResponse> getAllTags(
            @Query("id") String id
    );

    @GET("apis/api.php?action=fetchProducts")
    Call<MainResponse> fetchProducts(
            @Query("nextPageToken") int nextPageToken,
            @Query("keyword")@Nullable String keyword
    );

    @GET("apis/api.php?action=fetchProductNamesOnly")
    Call<SearchKeywordResponse> fetchProductNamesOnly(
            @Query("keyword")@Nullable String keyword
    );

    @GET("apis/api.php?action=getProductImages")
    Call<RoomImagesResponse> getRoomImages(
            @Query("id") String id
    );

    @GET("apis/api.php?action=getBanners")
    Call<BannerResponse> getBanners();

    @GET("apis/api.php?action=fetchCategories")
    Call<CategoryResponse> fetchCategories();

    @GET("apis/api.php?action=fetchSubCategories")
    Call<CategoryResponse> fetchSubCategories(
            @Query("parent_cat")@Nullable String parent_cat
    );

    @GET("apis/api.php?action=getVariantsByProductId")
    Call<VariantResponse> getVariantsByProductId(
            @Query("product_id") String product_id
    );

    @GET("apis/api.php?action=findProfile")
    Call<UserResponse> findProfile(
            @Query("keyword")@Nullable String keyword,
            @Query("nextToken") String nextToken
    );

    @GET("apis/api.php?action=getDashboardCounts")
    Call<CountResponse> getDashboardCounts();











}
