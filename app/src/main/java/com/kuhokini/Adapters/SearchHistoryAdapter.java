package com.kuhokini.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuhokini.Activities.SearchResult;
import com.kuhokini.Helpers.DB_Helper_Search_History;
import com.kuhokini.R;
import com.kuhokini.databinding.ChildHistoryBinding;

import java.util.ArrayList;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>{

    Activity activity;
    ArrayList<String> models;
    private DB_Helper_Search_History dbHelper;

    public SearchHistoryAdapter(Activity activity, ArrayList<String> models) {
        this.activity = activity;
        this.models = models;
        dbHelper = new DB_Helper_Search_History(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String searchHis = models.get(position);
        holder.binding.keyword.setText(searchHis);

        holder.binding.delete.setOnClickListener(v->{
            dbHelper.deleteSearchQuery(searchHis);
            models.remove(position);
            notifyDataSetChanged();
        });

        holder.binding.item.setOnClickListener(v->{
            Intent i = new Intent(activity.getApplicationContext(), SearchResult.class);
            i.putExtra("keyword", searchHis);
            activity.startActivity(i);
            activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildHistoryBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildHistoryBinding.bind(itemView);
        }
    }
}
