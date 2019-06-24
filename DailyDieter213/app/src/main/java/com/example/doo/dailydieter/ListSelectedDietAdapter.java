package com.example.doo.dailydieter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListSelectedDietAdapter extends BaseAdapter {

    Context context;
    ArrayList<ListSelectedDiet> arrayListSelectedDiet;
    TextView updateFoodName;
    TextView updateFoodCalorie;

    @Override
    public int getCount() {
        return arrayListSelectedDiet.size();
    }

    @Override
    public Object getItem(int position) {
        return this.arrayListSelectedDiet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_diet_selected,null);
            updateFoodName = convertView.findViewById(R.id.dietSelectedNameView);
            updateFoodCalorie = convertView.findViewById(R.id.dietSelectedCalorieView);
        }
        updateFoodName.setText(arrayListSelectedDiet.get(position).getNamefood());
        updateFoodCalorie.setText(String.valueOf(arrayListSelectedDiet.get(position).getCalorie()));




        return convertView;
    }

    public ListSelectedDietAdapter(Context context, ArrayList<ListSelectedDiet> arrayListUpdateDiet) {
        this.context = context;
        this.arrayListSelectedDiet = arrayListUpdateDiet;
    }
}
