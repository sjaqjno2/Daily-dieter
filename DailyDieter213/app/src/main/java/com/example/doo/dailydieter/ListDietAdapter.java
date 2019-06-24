package com.example.doo.dailydieter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;

import java.util.ArrayList;

import retrofit2.Callback;

public class ListDietAdapter extends BaseAdapter {
    Context context;
    ArrayList<ListDiet> arrayListDiet;
    TextView dietTextView;
    TextView dietMealTimeView;
    TextView dietDateView;

    public ListDietAdapter(Context context, ArrayList<ListDiet> arrayListDiet) {
        this.context = context;
        this.arrayListDiet = arrayListDiet;
    }

    @Override
    public int getCount() {
        return arrayListDiet.size();
    }

    @Override
    public Object getItem(int position) {
        return this.arrayListDiet.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.diet,null);
            dietMealTimeView = convertView.findViewById(R.id.dietMealTimeView);
            dietDateView = convertView.findViewById(R.id.dietDateView);
        }
        float sum_C = arrayListDiet.get(position).getCalorie1() + arrayListDiet.get(position).getCalorie2() +
                arrayListDiet.get(position).getCalorie3() + + arrayListDiet.get(position).getCalorie4() + arrayListDiet.get(position).getCalorie5();
        dietMealTimeView.setText(arrayListDiet.get(position).getMealtime());
        String result = "Total : " + String.valueOf(sum_C) + "kCal";
        dietDateView.setText(result);
        return convertView;
    }
}
