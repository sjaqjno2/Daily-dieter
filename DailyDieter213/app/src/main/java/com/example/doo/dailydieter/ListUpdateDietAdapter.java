package com.example.doo.dailydieter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.ArrayList;

public class ListUpdateDietAdapter extends BaseAdapter {

    Context context;
    ArrayList<ListUpdateDiet> arrayListUpdateDiet;
    EditText updateFoodName;
    EditText updateFoodCalorie;

    @Override
    public int getCount() {
        return arrayListUpdateDiet.size();
    }

    @Override
    public Object getItem(int position) {
        return this.arrayListUpdateDiet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_update,null);
            updateFoodName = convertView.findViewById(R.id.updateFoodNameView);
            updateFoodCalorie = convertView.findViewById(R.id.updateFoodCalorieVeiw);
        }
        updateFoodName.setText(arrayListUpdateDiet.get(position).getNamefood());
        updateFoodCalorie.setText(String.valueOf(arrayListUpdateDiet.get(position).getCalorie()));




        return convertView;
    }

    public ListUpdateDietAdapter(Context context, ArrayList<ListUpdateDiet> arrayListUpdateDiet) {
        this.context = context;
        this.arrayListUpdateDiet = arrayListUpdateDiet;
    }
}
