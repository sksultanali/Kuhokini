package com.kuhokini.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
//import com.hishd.tinycart.model.Cart;
//import com.hishd.tinycart.util.TinyCartHelper;
import com.kuhokini.APIModels.BannerResponse;
import com.kuhokini.APIModels.MainResponse;
import com.kuhokini.APIModels.ProductData;
import com.kuhokini.APIModels.ProductResponse;
import com.kuhokini.APIModels.VariantResponse;
import com.kuhokini.Adapters.FeaturedAdapter;
import com.kuhokini.Adapters.PhotosAdapter;
import com.kuhokini.Adapters.ProductsAdapter;
import com.kuhokini.Adapters.ReviewAdapter;
import com.kuhokini.Adapters.VariantAdapter;
import com.kuhokini.DeliveryModels.DelRetrofitClient;
import com.kuhokini.DeliveryModels.InvoiceResponse;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.DelhiveryApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.Precautions;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.MainActivity;
import com.kuhokini.Models.AddressResponse;
import com.kuhokini.Models.BannerModel;
import com.kuhokini.R;
import com.kuhokini.TinyCart.TinyCart;
import com.kuhokini.databinding.ActivityProductDetailsBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetails extends AppCompatActivity implements VariantAdapter.OnVariantClickListener {

    ActivityProductDetailsBinding binding;
    ApiService apiService;
    DelhiveryApiService delhiveryApiService;
    String productId;
    Activity activity;
    VariantResponse.Variant variantDetails;
    ArrayList<SlideModel> images = new ArrayList<>();
    int nextPageToken, currentPage = 0, productPrice;
    private boolean isInitialLoad = true;
    private boolean isLoading = false;
    private boolean delCriteriaVisible = false;
    TinyCart cart;
    ProductData details;
    ProductsAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());
        activity = ProductDetails.this;
        cart = TinyCart.getInstance();
        apiService = RetrofitClient.getClient().create(ApiService.class);
        delhiveryApiService = DelRetrofitClient.getApiService();
        productId = Helper.getData(ProductDetails.this, "product_id");
        images.add(new SlideModel(R.drawable.placeholder, ScaleTypes.CENTER_CROP));
        progressDialog = new ProgressDialog(ProductDetails.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Checking availability...");

        binding.seeAllReview.setOnClickListener(v->{
            Intent i = new Intent(ProductDetails.this, ReviewsActivity.class);
            i.putExtra("id", productId);
            startActivity(i);
        });

        LinearLayoutManager lnm = new LinearLayoutManager(ProductDetails.this);
        lnm.setOrientation(RecyclerView.HORIZONTAL);
        binding.variantRec.setLayoutManager(lnm);

        Call<ProductResponse> call = apiService.getProductDetails(productId);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    ProductResponse productResponse = response.body();
                    if (productResponse.getStatus().equalsIgnoreCase("success")){

                        details = productResponse.getData();
                        binding.availVariant.setText("Available Variant : " + details.getVariants().size());
                        VariantAdapter adapter = new VariantAdapter(ProductDetails.this, details.getVariants(),
                                ProductDetails.this);
                        binding.variantRec.setAdapter(adapter);

                        variantDetails = details.getVariants().get(0);
                        Precautions.WEIGHT = details.getWeight();
                        setVariantInfo(variantDetails);
                        getSimilarPost(String.valueOf(details.getCat_id()), String.valueOf(details.getSub_cat_id()));

                        variantDetails.setRate(details.getRating_info().getAverage_rating());
                        variantDetails.setTotalRate(details.getRating_info().getTotal_ratings());
                        variantDetails.setWeight(details.getWeight());

                        if (details.getRating_info().getAverage_rating() == 1){
                            binding.star1.setVisibility(View.VISIBLE);
                        } else if (details.getRating_info().getAverage_rating() == 2) {
                            binding.star1.setVisibility(View.VISIBLE);
                            binding.star2.setVisibility(View.VISIBLE);
                        }else if (details.getRating_info().getAverage_rating() == 3) {
                            binding.star1.setVisibility(View.VISIBLE);
                            binding.star2.setVisibility(View.VISIBLE);
                            binding.star3.setVisibility(View.VISIBLE);
                        }else if (details.getRating_info().getAverage_rating() == 4) {
                            binding.star1.setVisibility(View.VISIBLE);
                            binding.star2.setVisibility(View.VISIBLE);
                            binding.star3.setVisibility(View.VISIBLE);
                            binding.star4.setVisibility(View.VISIBLE);
                        }else if (details.getRating_info().getAverage_rating() == 5
                                || details.getRating_info().getAverage_rating() == 0) {
                            binding.star1.setVisibility(View.VISIBLE);
                            binding.star2.setVisibility(View.VISIBLE);
                            binding.star3.setVisibility(View.VISIBLE);
                            binding.star4.setVisibility(View.VISIBLE);
                            binding.star5.setVisibility(View.VISIBLE);
                        }

                        binding.ratingLabel.setText(details.getRating_info().getRating_label());

                        binding.name.setText(details.getProduct_name());
                        binding.rating.setText(details.getRating_info().getAverage_rating() + " ("
                                + details.getRating_info().getReview_count() +" Reviews)");
                        binding.avgRating.setText(details.getRating_info().getAverage_rating() + "/5");
                        binding.totalRating.setText(details.getRating_info().getReview_count() + " Reviews");
                        binding.reviewCount.setText("Review ("+details.getRating_info().getReview_count()+"+)");

                        if (details.getRating_info().getReview_count() == 0){
                            binding.reviewSegment.setVisibility(View.GONE);
                            binding.reviewsRecyclerView.setVisibility(View.GONE);
                        }else {
                            binding.reviewSegment.setVisibility(View.VISIBLE);
                            binding.reviewsRecyclerView.setVisibility(View.VISIBLE);

                            LinearLayoutManager lnm = new LinearLayoutManager(ProductDetails.this);
                            binding.reviewsRecyclerView.setLayoutManager(lnm);
                            ReviewAdapter adapter1 = new ReviewAdapter(ProductDetails.this, details.getLatest_reviews());
                            binding.reviewsRecyclerView.setAdapter(adapter1);
                        }

                        if (details.getRating_info().getReview_count() < 3){
                            binding.seeAllReview.setVisibility(View.GONE);
                        }else {
                            binding.seeAllReview.setVisibility(View.VISIBLE);
                            binding.sellAllReviewTxt.setText("See All "+ details.getRating_info().getReview_count() +" Reviews");
                        }

                        binding.Star1.setText(String.valueOf(details.getRating_info().getRating_distribution().getOne_star()));
                        binding.Star2.setText(String.valueOf(details.getRating_info().getRating_distribution().getTwo_star()));
                        binding.Star3.setText(String.valueOf(details.getRating_info().getRating_distribution().getThree_star()));
                        binding.Star4.setText(String.valueOf(details.getRating_info().getRating_distribution().getFour_star()));
                        binding.Star5.setText(String.valueOf(details.getRating_info().getRating_distribution().getFive_star()));

                        binding.seekBar1.setProgress(details.getRating_info().getRating_distribution().getOne_star());
                        binding.seekBar2.setProgress(details.getRating_info().getRating_distribution().getTwo_star());
                        binding.seekBar3.setProgress(details.getRating_info().getRating_distribution().getThree_star());
                        binding.seekBar4.setProgress(details.getRating_info().getRating_distribution().getFour_star());
                        binding.seekBar5.setProgress(details.getRating_info().getRating_distribution().getFive_star());

                        binding.seekBar1.setMax(details.getRating_info().getTotal_ratings());
                        binding.seekBar2.setMax(details.getRating_info().getTotal_ratings());
                        binding.seekBar3.setMax(details.getRating_info().getTotal_ratings());
                        binding.seekBar4.setMax(details.getRating_info().getTotal_ratings());
                        binding.seekBar5.setMax(details.getRating_info().getTotal_ratings());

                        binding.seekBar1.setOnTouchListener((v, event) -> true);
                        binding.seekBar2.setOnTouchListener((v, event) -> true);
                        binding.seekBar3.setOnTouchListener((v, event) -> true);
                        binding.seekBar4.setOnTouchListener((v, event) -> true);
                        binding.seekBar5.setOnTouchListener((v, event) -> true);


//                        Markwon markwon = Markwon.create(ProductDetails.this);
//                        markwon.setMarkdown(binding.description, details.getDescription());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.description.setText(
                                    Html.fromHtml(details.getDescription(), Html.FROM_HTML_MODE_COMPACT)
                            );
                        } else {
                            binding.description.setText(
                                    Html.fromHtml(details.getDescription())
                            );
                        }



                    }else {

                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {

            }
        });

        binding.deliveryCriteria.setOnClickListener(v->{
            if (delCriteriaVisible){
                binding.delCriteriaLayout.setVisibility(View.GONE);
                delCriteriaVisible = false;
                binding.delArrow.setRotation(0);
            }else {
                binding.delCriteriaLayout.setVisibility(View.VISIBLE);
                delCriteriaVisible = true;
                binding.delArrow.setRotation(180);
            }
        });

        binding.payToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (binding.cod.isChecked()){
                    binding.cashExtra.setText("₹" + Precautions.CASH_PAYMENT_CHARGES + " will be charged extra for Cash on Delivery!");
                    Precautions.CASH_PAYMENT = true;
                }else {
                    binding.cashExtra.setText("No extra charges for prepaid orders!");
                    Precautions.CASH_PAYMENT = false;
                }
                setPrice();
            }
        });

        binding.delBy.setText(Helper.getFutureDate(10));
        binding.matToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (binding.expressD.isChecked()){
                    binding.deliveryExpR.setText(Precautions.EXPRESS_DELIVERY_DAYS);
                    binding.delBy.setText(Helper.getFutureDate(7));
                    Precautions.REGULAR_DELIVERY = false;
                }else {
                    binding.deliveryExpR.setText(Precautions.REGULAR_DELIVERY_DAYS);
                    binding.delBy.setText(Helper.getFutureDate(10));
                    Precautions.REGULAR_DELIVERY = true;
                }
                setPrice();
            }
        });

        binding.pinCodeCheck.setOnClickListener(v->{
            String pin = binding.pinEdText.getText().toString();
            if (pin.isEmpty()){
                binding.pinEdText.setError("*");
            }else {
                Helper.hideKeyboard(ProductDetails.this);
                checkPinCode(pin);
            }
        });

        binding.delSec.setOnClickListener(v->{
            Helper.showOnlyBottomMessage(ProductDetails.this, "Delivery Type",
                    "If you choose <b>Express</b> option. "+
                            Precautions.EXPRESS_DELIVERY_DAYS +
                            "(Exp. delivery by <b>"+ Helper.getFutureDate(7) +
                            "</b>)<br>And if you choose <b>Regular</b> option. "+
                    Precautions.REGULAR_DELIVERY_DAYS + "(Exp. delivery by <b>"+ Helper.getFutureDate(10)+")");
        });

        binding.returnSec.setOnClickListener(v->{
            Helper.showBottomAction(ProductDetails.this, "Return Policy",
                    Precautions.RETURN_POLICY_DES, "Terms & Conditions", null, true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {
                            Helper.openLink(ProductDetails.this, "https://kuhokini.com/return-policy.php");
                        }
                        @Override
                        public void onNoButtonClicked() {

                        }
                        @Override
                        public void onCloseButtonClicked() {

                        }
                    });
        });

        binding.cashSec.setOnClickListener(v->{
            Helper.showOnlyBottomMessage(ProductDetails.this, "Cash on Delivery", Precautions.COD_DES);
        });


//        getLifecycle().addObserver(binding.youtubePostView);
//        binding.youtubePostView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                youTubePlayer.loadVideo(videoModel.getYouTubeId(), 0f);
//            }
//        });

        setupRecyclerView();
        if (isInitialLoad) {
            getPostData();
            isInitialLoad = false; // Mark initial load as complete
        }
        imageSliders();

    }


    private void setVariantInfo(VariantResponse.Variant variantDetails) {
        if (variantDetails.getImages() != null && !variantDetails.getImages().isEmpty()){
            binding.imageView.setImageList(variantDetails.getImages(), ScaleTypes.CENTER_CROP);
        }else {
            binding.imageView.setImageList(images, ScaleTypes.CENTER_CROP);
        }

        if (variantDetails.getStock() > 0){
            binding.stockAvailable.setVisibility(View.VISIBLE);
            binding.outOfStock.setVisibility(View.GONE);
        }else {
            binding.stockAvailable.setVisibility(View.GONE);
            binding.outOfStock.setVisibility(View.VISIBLE);
        }

        binding.buyBtn.setOnClickListener(v->{
            Intent i = new Intent(ProductDetails.this, CheckOut.class);
            i.putExtra("price", variantDetails.getSelling_price());
            i.putExtra("weight", details.getWeight());
            startActivity(i);
        });

        binding.variantName.setText(variantDetails.getVarient_name());
        binding.normalPrice.setPaintFlags(binding.normalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        binding.addToCart.setOnClickListener(v->{
            cart.addItem(variantDetails, details.getProduct_name(), variantDetails.getSelling_price(), details.getWeight());
            binding.addToCart.setEnabled(false);
            new CountDownTimer(3000, 1000) {
                int count = 3;
                @Override
                public void onTick(long millisUntilFinished) {
                    binding.addToCartTxt.setText("Added (" + count + ")");
                    count--;
                }
                @Override
                public void onFinish() {
                    binding.addToCart.setEnabled(true);
                    binding.addToCartTxt.setText("Add Again");
                }
            }.start();
        });

        Helper.startCounter(variantDetails.getNormal_price(), binding.normalPrice, "₹", "");
        Helper.startCounter(variantDetails.getSelling_price(), binding.sellingPrice, "₹", "");

        int saveAmount = variantDetails.getNormal_price() - variantDetails.getSelling_price();
        productPrice = variantDetails.getSelling_price();
        int discount = (int)(
                (saveAmount * 100) /variantDetails.getNormal_price()
        );
        binding.discountPrice.setText("Save ₹" + saveAmount);
        binding.offer.setText(discount+"% off");
        //Helper.startCounter(discount, binding.offer, "", "% off");

//        LinearLayoutManager lnm = new LinearLayoutManager(ProductDetails.this);
//        lnm.setOrientation(RecyclerView.HORIZONTAL);
//        binding.reviewImages.setLayoutManager(lnm);
//        PhotosAdapter adapter = new PhotosAdapter(ProductDetails.this, variantDetails.getImages());
//        binding.reviewImages.setAdapter(adapter);

        binding.whatsappEnq.setOnClickListener(v->{
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain"); // Use "text/plain" as WhatsApp might not support "text/html"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, Precautions.generateShareMessage(ProductDetails.this, true, variantDetails, details.getProduct_name()));
            sharingIntent.setPackage("com.whatsapp");
            if (sharingIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(sharingIntent);
            } else {
                // Handle the case where WhatsApp is not installed
                Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
            }
        });

        binding.shareLin.setOnClickListener(v->binding.shareBtn.performClick());
        binding.shareBtn.setOnClickListener(v->{
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, Precautions.generateShareMessage(ProductDetails.this, false, variantDetails, details.getProduct_name()));
            if (sharingIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
            }
        });

        setPrice();

    }

    private void setPrice() {
        int fPrice = productPrice;

        if (productPrice >= Precautions.FREE_DELIVERY_AFTER) {
            fPrice += 0;
            binding.freeDelivery.setText("Free Delivery");
            binding.extraNote.setText("Free Delivery");
            binding.deliveryPrice.setVisibility(View.GONE);
        }else {
            if (Precautions.REGULAR_DELIVERY) {
                fPrice += Precautions.SURFACE_DELIVERY_CHARGES;
                binding.deliveryPrice.setText(Precautions.SURFACE_DELIVERY_CHARGES + " only");
                binding.extraNote.setText("Delivery charges ₹" + Precautions.SURFACE_DELIVERY_CHARGES + " only");
            }else {
                fPrice += Precautions.EXPRESS_DELIVERY_CHARGES;
                binding.deliveryPrice.setText(Precautions.EXPRESS_DELIVERY_CHARGES + " only");
                binding.extraNote.setText("Delivery charges ₹" + Precautions.EXPRESS_DELIVERY_CHARGES + " only");
            }
            binding.freeDelivery.setText("Delivery charges ");
            binding.deliveryPrice.setVisibility(View.VISIBLE);
        }

        if (Precautions.CASH_PAYMENT) {
            fPrice += Precautions.CASH_PAYMENT_CHARGES;
        }

        //binding.subtotal.setText(String.valueOf(fPrice));
        binding.sellingPrice.setText(String.valueOf(fPrice));
    }

    void checkPinCode(String pin){
        String type, payType;
        if (Precautions.REGULAR_DELIVERY){
            type = "S";
        }else {
            type = "E";
        }

        if (Precautions.CASH_PAYMENT){
            payType = "COD";
        }else {
            payType = "Pre-paid";
        }

        Call<List<InvoiceResponse>> call = delhiveryApiService.getInvoiceCharges(
                "Token a648f55858a9b418c8573b2dd7532cb2383e8360", type, "Delivered", pin, "743332", Precautions.WEIGHT, payType);
        Log.d("PIN_CHECK", "Sending request to: " + call.request().url());
        progressDialog.show();
        call.enqueue(new Callback<List<InvoiceResponse>>() {
            @Override
            public void onResponse(Call<List<InvoiceResponse>> call, Response<List<InvoiceResponse>> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    InvoiceResponse response1 = response.body().get(0);  // get first item from array

                    if (response1.isSuccessful()) {
                        if (response1.getStatus() != null && response1.getStatus().equalsIgnoreCase("Delivered")) {

                            binding.availablePinText.setText("Good to Go! Available in your pin-code...! 😃");

                            try {
                                double amount = response1.getTotal_amount();
                                if (Precautions.CASH_PAYMENT){
                                    if (type.equalsIgnoreCase("E")) {
                                        Precautions.EXPRESS_DELIVERY_CHARGES = Math.toIntExact(Math.round(amount)) + Precautions.EXTRA_SAFETY_DELIVERY_CHARGES;
                                    } else {
                                        Precautions.SURFACE_DELIVERY_CHARGES = Math.toIntExact(Math.round(amount)) + Precautions.EXTRA_SAFETY_DELIVERY_CHARGES;
                                    }
                                }
                                binding.deliveryPrice.setAnimation(AnimationUtils.loadAnimation(ProductDetails.this, R.anim.blink));
                                setPrice();
                            } catch (ArithmeticException e) {
                                Helper.showOnlyMessage(ProductDetails.this, "Error", "Delivery charge too large");
                            }
                        } else {
                            binding.availablePinText.setVisibility(View.GONE);
                            Helper.showOnlyMessage(ProductDetails.this, "Error", "No delivery data available ");
                        }
                    } else {
                        binding.availablePinText.setVisibility(View.GONE);
                        Helper.showOnlyMessage(ProductDetails.this, "Failed", response1.toString());
                    }
                } else {
                    binding.availablePinText.setVisibility(View.GONE);
                    Helper.showOnlyMessage(ProductDetails.this, "Not Deliverable", "We're sorry but we are not available in your pin-code. May be in some other day.<br><br>Thank you,<br>Team Kuhokini");
                }
            }

            @Override
            public void onFailure(Call<List<InvoiceResponse>> call, Throwable t) {
                progressDialog.dismiss();
                binding.availablePinText.setVisibility(View.GONE);
                Helper.showOnlyMessage(ProductDetails.this, "Error",
                        "Something went wrong. " + t.getLocalizedMessage() + " Please try again later");
            }
        });


    }

    @Override
    public void onChange(VariantResponse.Variant variant) {
        setVariantInfo(variant);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(ProductDetails.this, 2);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setItemViewCacheSize(50);
        binding.recyclerview.setDrawingCacheEnabled(true);
        binding.recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && nextPageToken != 0) {
                    isLoading = true;
                    getPostData();
                }
            }
        });
    }

    void getSimilarPost(String catId, String subCatId) {
        LinearLayoutManager lnm = new LinearLayoutManager(ProductDetails.this);
        lnm.setOrientation(RecyclerView.HORIZONTAL);
        binding.similarRecyclerView.setLayoutManager(lnm);
        binding.similarRecyclerView.showShimmerAdapter();

        Call<MainResponse> call = apiService.fetchProductsByCategory(0, catId, subCatId);
        call.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MainResponse apiResponse = response.body();
                    if (apiResponse.getStatus().equalsIgnoreCase("success")) {
                        if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                            FeaturedAdapter simAdapter = new FeaturedAdapter(ProductDetails.this, apiResponse.getData());
                            binding.similarRecyclerView.setAdapter(simAdapter);
                            binding.similarLin.setVisibility(View.VISIBLE);
                        }else {
                            binding.similarLin.setVisibility(View.GONE);
                        }
                    }else {
                        binding.similarLin.setVisibility(View.GONE);
                    }
                }
                binding.similarRecyclerView.hideShimmerAdapter();
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                binding.similarLin.setVisibility(View.GONE);
            }
        });
    }

    void getPostData() {
        binding.loadMore.setVisibility(View.VISIBLE);
//        binding.noData.setVisibility(View.GONE); // Hide noData initially

        if (nextPageToken == 0 && adapter != null) {
            adapter.clearItems(); // clear old data on new search
            binding.similarRecyclerView.showShimmerAdapter();
            binding.recyclerview.showShimmerAdapter();
        }

        Call<MainResponse> call = apiService.fetchProducts(nextPageToken, null, null);
        call.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MainResponse apiResponse = response.body();
                    if (apiResponse.getStatus().equalsIgnoreCase("success")) {
                        if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                            if (adapter == null) {
                                adapter = new ProductsAdapter(ProductDetails.this, apiResponse.getData(),
                                        new ProductsAdapter.OnCartChangedListener() {
                                            @Override
                                            public void onCartChanged() {
                                                //updateCartBadge();
                                            }
                                        });
                                binding.recyclerview.setAdapter(adapter);
                                binding.similarRecyclerView.hideShimmerAdapter();
                                binding.recyclerview.hideShimmerAdapter();
                            } else {
                                if (nextPageToken == 0) {
                                    adapter.setItems(apiResponse.getData()); // replace data
                                } else {
                                    adapter.addItems(apiResponse.getData()); // append data
                                }
                            }

                            if (apiResponse.getNextPageToken() != null) {
                                nextPageToken = Integer.parseInt(apiResponse.getNextPageToken());
                                currentPage = (nextPageToken / 15);
                            }

                            if (apiResponse.getData().size() < 15) {
                                nextPageToken = 0; // No more data to load
                            }
                        }
                    }
                }
                binding.loadMore.setVisibility(View.GONE);
                isLoading = false;
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                //binding.noData.setVisibility(View.VISIBLE);
                binding.loadMore.setVisibility(View.GONE);
                binding.similarRecyclerView.hideShimmerAdapter();
                binding.recyclerview.hideShimmerAdapter();
                isLoading = false;
            }
        });
    }

    void imageSliders(){
        Call<BannerResponse> call = apiService.getBanners();
        call.enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                if (response.isSuccessful() && response != null){
                    BannerResponse categoryResponse = response.body();
                    if (categoryResponse.getStatus().equalsIgnoreCase("success")){
                        binding.viewFlipper.removeAllViews();
                        for (BannerModel bannerModel : categoryResponse.getData()) {
                            ImageView imageView = new ImageView(ProductDetails.this);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            if (!activity.isDestroyed()){
                                Glide.with(ProductDetails.this)
                                        .load(bannerModel.getImageUrl())
                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                        .placeholder(R.drawable.placeholder) // Optional placeholder
                                        .into(imageView);
                            }

                            imageView.setOnClickListener(view -> {
                                int currentIndex = binding.viewFlipper.getDisplayedChild(); // Get current displayed child index
                                BannerModel activeBanner = categoryResponse.getData().get(currentIndex);
                                if (activeBanner.getMessage() == null && activeBanner.getLink() == null){
                                    Toast.makeText(ProductDetails.this, "No link attached!", Toast.LENGTH_SHORT).show();
                                }else {
                                    showBannerDialog(activeBanner.getMessage(), activeBanner.getLink(), activeBanner.getBtnTxt());                                        }
                            });
                            binding.viewFlipper.addView(imageView);
                        }

                        binding.viewFlipper.setFlipInterval(5000);
                        binding.viewFlipper.startFlipping();

                        binding.cardView.setVisibility(View.VISIBLE);
                    }else {
                        binding.cardView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                binding.cardView.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void showBannerDialog(String message, String link, String btnTxt) {

        String btn = "Open Link";
        if (btnTxt != null && !btnTxt.isEmpty()){
            btn = btnTxt;
        }

        if (message != null && !message.isEmpty() && (link == null || link.isEmpty())){
            Helper.showActionDialog(ProductDetails.this, "Message Found", message,
                    "Okay", null, true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {

                        }

                        @Override
                        public void onNoButtonClicked() {

                        }

                        @Override
                        public void onCloseButtonClicked() {

                        }
                    });
        }else if (message != null && !message.isEmpty() && (link != null || !link.isEmpty())){
            Helper.showActionDialog(ProductDetails.this, "Message Found", message,
                    btn, null, true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {
                            Helper.openLink(ProductDetails.this, link);
                        }

                        @Override
                        public void onNoButtonClicked() {

                        }

                        @Override
                        public void onCloseButtonClicked() {

                        }
                    });
        }else {
            Helper.showActionDialog(ProductDetails.this, "Link Found", "For opening attach link, click below button. " +
                            "It will redirect you to the linked page!",
                    btn, null, true, new Helper.DialogButtonClickListener() {
                        @Override
                        public void onYesButtonClicked() {
                            Helper.openLink(ProductDetails.this, link);
                        }

                        @Override
                        public void onNoButtonClicked() {

                        }

                        @Override
                        public void onCloseButtonClicked() {

                        }
                    });
        }
    }

}