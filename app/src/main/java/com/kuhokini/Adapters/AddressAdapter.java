package com.kuhokini.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuhokini.Helpers.ApiResponse;
import com.kuhokini.Helpers.ApiService;
import com.kuhokini.Helpers.Helper;
import com.kuhokini.Models.AddressResponse;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildAddressBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder>{

    Activity activity;
    List<AddressResponse.AddressModel> models;
    ProgressDialog dialog;
    ApiService apiService;
    OnChangeListener onChangeListener;

    public interface OnChangeListener{
        void onChangePrimary();
        void onSelect(AddressResponse.AddressModel addressModel);
    }

    public AddressAdapter(Activity activity, List<AddressResponse.AddressModel> models,
                          ApiService apiService, ProgressDialog dialog, OnChangeListener onChangeListener) {
        this.activity = activity;
        this.models = models;
        this.dialog = dialog;
        this.apiService = apiService;
        this.onChangeListener = onChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressResponse.AddressModel addressModel = models.get(position);

        holder.binding.name.setText(addressModel.getName()+", " + addressModel.getPin());
        if (addressModel.getLandmark() != null && !addressModel.getLandmark().isEmpty()) {
            holder.binding.address.setText(addressModel.getAddress() + ", "+ addressModel.getLandmark()+", "
                    + addressModel.getState() + "\n" +addressModel.getPhone());
        }else {
            holder.binding.address.setText(addressModel.getAddress() + ", "
                    + addressModel.getState() + "\n" +addressModel.getPhone());
        }

        if (addressModel.getStatus() == 1){
            holder.binding.primary.setVisibility(View.VISIBLE);
        }else {
            holder.binding.primary.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Helper.showActionDialog(activity,
                        "Perform Action",
                        "<center>Select below option's to make changes in address. You can delete address or you can make this address as primary address.</center>",
                        "Make Primary", "Delete", true, new Helper.DialogButtonClickListener() {
                            @Override
                            public void onYesButtonClicked() {
                                Call<ApiResponse> call = apiService.updateAddressStatus(addressModel.getUser_id(), addressModel.getId());
                                dialog.show();
                                Helper.executeCall(call, activity, dialog);
                                onChangeListener.onChangePrimary();
                            }

                            @Override
                            public void onNoButtonClicked() {
                                Call<ApiResponse> call = apiService.deleteTable("addresses", addressModel.getId());
                                dialog.show();
                                Helper.executeCall(call, activity, dialog);
                                onChangeListener.onChangePrimary();
                            }
                            @Override
                            public void onCloseButtonClicked() {}
                        }
                );

                return true;
            }
        });

        holder.itemView.setOnClickListener(v->{
            onChangeListener.onSelect(addressModel);
        });


    }


    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildAddressBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildAddressBinding.bind(itemView);
        }
    }
}
