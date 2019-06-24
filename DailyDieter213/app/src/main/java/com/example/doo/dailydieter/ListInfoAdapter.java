package com.example.doo.dailydieter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListInfoAdapter extends BaseAdapter {

    Context context;
    ArrayList<ListInfo> arrayListInfo;
    TextView categoryView;
    TextView categoryValueView;

    public ListInfoAdapter(Context context, ArrayList<ListInfo> arrayListInfo) {
        this.context = context;
        this.arrayListInfo = arrayListInfo;
    }
    @Override
    public int getCount() {
        return arrayListInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return this.arrayListInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_info,null);
            categoryView = convertView.findViewById(R.id.categoryView);
            categoryValueView = convertView.findViewById(R.id.categoryValueView);
        }
        categoryView.setText(arrayListInfo.get(position).getCategory());
        categoryValueView.setText(arrayListInfo.get(position).getCategoryValue());
        return convertView;
    }
}

