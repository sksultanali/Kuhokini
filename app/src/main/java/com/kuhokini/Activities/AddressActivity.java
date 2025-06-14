package com.kuhokini.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kuhokini.Adapters.AddressAdapter;
import com.kuhokini.Helpers.ApiResponse;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Helpers.RetrofitClient;
import com.kuhokini.Models.AddressResponse;
import com.kuhokini.Models.StateModel;
import com.kuhokini.R;
import com.kuhokini.databinding.ActivityAddressBinding;
import com.kuhokini.databinding.AddressDialogBoxBinding;
import com.kuhokini.databinding.DialogAddressBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.OnChangeListener{

    ActivityAddressBinding binding;
    ApiService apiService;
    String state;
    ProgressDialog progressDialog;
    ArrayAdapter<StateModel> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.goBack.setOnClickListener(v->onBackPressed());
        progressDialog = new ProgressDialog(AddressActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Connecting server...");
        apiService = RetrofitClient.getClient().create(ApiService.class);
        binding.addressRec.setLayoutManager(new LinearLayoutManager(AddressActivity.this));

        binding.addAddress.setOnClickListener(v->{
            showAddressDialog();
        });

        loadAddresses();

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simulate data reload
            new Handler().postDelayed(() -> {
                loadAddresses();
                binding.swipeRefreshLayout.setRefreshing(false); // Stop spinner
            }, 1500);
        });


    }

    private void showAddressDialog() {
        DialogAddressBinding addressBinding = DialogAddressBinding.inflate(getLayoutInflater());

        // Create a new dialog and set the custom layout
        Dialog dialog = new Dialog(this);
        dialog.setContentView(addressBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        addressBinding.name.postDelayed(new Runnable() {
            @Override
            public void run() {
                addressBinding.name.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(addressBinding.name, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);

        loadStates(addressBinding);

        addressBinding.save.setOnClickListener(v->{
            String userId = "1";
            String name = addressBinding.name.getText().toString();
            String phone = addressBinding.phone.getText().toString();
            String address = addressBinding.address.getText().toString();
            String pinCode = addressBinding.pincode.getText().toString();
            String landmark = addressBinding.landmark.getText().toString();

            if (name.isEmpty()){
                addressBinding.name.setError("*");
            }else if (phone.isEmpty()){
                addressBinding.phone.setError("*");
            }else if (address.isEmpty()){
                addressBinding.address.setError("*");
            }else if (pinCode.isEmpty()){
                addressBinding.pincode.setError("*");
            }else if (state == null ){
                Helper.showActionDialog(AddressActivity.this, "Error", "Please select state name from the dropdown menu. You can not proceed further without state entry.",
                        "Okay", null, false, new Helper.DialogButtonClickListener() {
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
            }else {

                progressDialog.show();

                Call<ApiResponse> call = apiService.insertAddress(name, userId, phone, state, pinCode, address, landmark);

                // Log constructed call details
//                Log.d("API_CALL", "insertAddress called with -> " +
//                        "name=" + name + ", userId=" + userId + ", phone=" + phone +
//                        ", state=" + state + ", pinCode=" + pinCode + ", address=" + address + ", landmark=" + landmark);

                call.enqueue(new Callback<ApiResponse>() {
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        //Log.d("API_RESPONSE", "onResponse called");

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            //Log.d("API_RESPONSE", "Success: " + apiResponse.getStatus());

                            if (!apiResponse.getStatus().equalsIgnoreCase("success")) {
                                Helper.showActionDialog(AddressActivity.this, apiResponse.getStatus(),
                                        apiResponse.getMessage(), "Okay", null, true, new Helper.DialogButtonClickListener() {
                                            public void onYesButtonClicked() {}
                                            public void onNoButtonClicked() {}
                                            public void onCloseButtonClicked() {}
                                        });
                            } else {
                                //Log.d("ADDRESS_FLOW", "Address added, loading all addresses...");
                                loadAddresses();
                            }
                        } else {
                            //Log.d("API_RESPONSE", "API failure response: " + response.message());
                        }

                        progressDialog.dismiss();
                        dialog.dismiss();
                    }

                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        //Log.e("API_ERROR", "onFailure: " + t.getMessage());

                        Helper.showActionDialog(AddressActivity.this, "Failed",
                                t.getLocalizedMessage(), "Okay", null, true, new Helper.DialogButtonClickListener() {
                                    public void onYesButtonClicked() {}
                                    public void onNoButtonClicked() {}
                                    public void onCloseButtonClicked() {}
                                });

                        dialog.dismiss();
                        progressDialog.dismiss();
                    }
                });

            }
        });

        // Show the dialog
        dialog.show();
    }

    private void loadStates(DialogAddressBinding addressBinding) {
        String url = "https://www.jsonkeeper.com/b/1RW6";
        List<StateModel> stateModels = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                String value = object.getString("value");
                                String label = object.getString("label");
                                stateModels.add(new StateModel(value, label));
                            }

                            // Create adapter after data is loaded
                            ArrayAdapter<StateModel> adapter = new ArrayAdapter<>(
                                    AddressActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    stateModels
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            addressBinding.stateSpinner.setAdapter(adapter);

                            addressBinding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    StateModel selectedState = adapter.getItem(position);
                                    if (selectedState != null) {
                                        state = selectedState.getValue(); // You must declare `state` variable in your class
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

                            // Optionally select first item
                            addressBinding.stateSpinner.post(() -> {
                                addressBinding.stateSpinner.setSelection(0, false);
                                StateModel selectedState = adapter.getItem(0);
                                if (selectedState != null) {
                                    state = selectedState.getValue();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddressActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddressActivity.this, "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(AddressActivity.this);
        queue.add(jsonArrayRequest);
    }

    private void loadAddresses() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.addressRec.setVisibility(View.GONE);
        Call<AddressResponse> call = apiService.getAddresses("1");
        call.enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    AddressResponse addressResponse = response.body();
                    if (addressResponse.getStatus().equalsIgnoreCase("success")){
                        AddressAdapter addressAdapter = new AddressAdapter(AddressActivity.this, addressResponse.getAddresses(),
                                apiService, progressDialog, AddressActivity.this);
                        binding.addressRec.setAdapter(addressAdapter);
                        if (addressResponse.getAddresses().isEmpty()){
                            binding.addressRec.setVisibility(View.GONE);
                            binding.noData.setVisibility(View.VISIBLE);
                        }else {
                            binding.addressRec.setVisibility(View.VISIBLE);
                            binding.noData.setVisibility(View.GONE);
                        }
                    }else {
                        binding.addressRec.setVisibility(View.GONE);
                        binding.noData.setVisibility(View.VISIBLE);
                    }
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.addressRec.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void changed() {
        loadAddresses();
    }
}