package com.kuhokini.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.kuhokini.Models.ToolsModel;
import com.kuhokini.R;

import java.util.ArrayList;

public class myListAdapter extends BaseAdapter {

    Activity activity;
    ArrayList<ToolsModel> arrayList;

    public myListAdapter(Activity activity, ArrayList<ToolsModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater obj = activity.getLayoutInflater();
        View view1 = obj.inflate(R.layout.sample_tools, null);
        ImageView imageView = view1.findViewById(R.id.toolImg);
        TextView textView = view1.findViewById(R.id.toolName);

        ToolsModel toolsModel = arrayList.get(i);
        textView.setText(toolsModel.getName());
        imageView.setImageDrawable(toolsModel.getDrawable());

        return view1;
    }
}
