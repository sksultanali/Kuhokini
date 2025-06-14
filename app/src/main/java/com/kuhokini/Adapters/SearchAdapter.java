package com.kuhokini.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuhokini.APIModels.SearchKeywordResponse;
import com.kuhokini.Activities.SearchResult;
import com.kuhokini.Helpers.DB_Helper_Search_History;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildKeywordBinding;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    Activity activity;
    List<SearchKeywordResponse.Data> models;
    private DB_Helper_Search_History dbHelper;

    public SearchAdapter(Activity activity, List<SearchKeywordResponse.Data> models) {
        this.activity = activity;
        this.models = models;
        dbHelper = new DB_Helper_Search_History(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_keyword, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchKeywordResponse.Data details = models.get(position);

        holder.binding.keyword.setText(details.getProduct_name());
        holder.binding.keywordEx.setText(details.getVariant_name());

        holder.binding.item.setOnClickListener(v->{
            dbHelper.addSearchQuery(details.getProduct_name());
            Intent i = new Intent(activity.getApplicationContext(), SearchResult.class);
            i.putExtra("keyword", details.getProduct_name());
            activity.startActivity(i);
            activity.finish();
        });


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildKeywordBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildKeywordBinding.bind(itemView);
        }
    }
}
