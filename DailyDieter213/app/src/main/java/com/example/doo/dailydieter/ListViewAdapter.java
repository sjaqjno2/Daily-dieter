package com.example.doo.dailydieter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Nutrient> nutrientList = null;
    private ArrayList<Nutrient> arraylist;

    public ListViewAdapter(Context context, List<Nutrient> nutrientList) {
        mContext = context;
        this.nutrientList = nutrientList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Nutrient>();
        this.arraylist.addAll(nutrientList);
    }

    public class ViewHolder {
        TextView namefood;
        TextView carbohydrate;
        TextView protein;
        TextView fat;
        TextView salt;
        TextView calorie;
        TextView weightfood;
    }

    @Override
    public int getCount() {
        return nutrientList.size();
    }

    @Override
    public Nutrient getItem(int position) {
        return nutrientList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item_diet_process, null);
            // Locate the TextViews in listview_item_diet_process.xml
            holder.namefood = (TextView) view.findViewById(R.id.namefood);
            holder.carbohydrate = (TextView) view.findViewById(R.id.carbohydrate);
            holder.protein = (TextView) view.findViewById(R.id.protein);
            holder.fat = (TextView) view.findViewById(R.id.fat);
            holder.salt = (TextView) view.findViewById(R.id.salt);
            holder.calorie = (TextView) view.findViewById(R.id.calorie);
            holder.weightfood = (TextView) view.findViewById(R.id.weightfood);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.namefood.setText(nutrientList.get(position).getNamefood());
        holder.carbohydrate.setText(String.valueOf(nutrientList.get(position).getCarbohydrate()));
        holder.protein.setText((String.valueOf(nutrientList.get(position).getProtein())));
        holder.fat.setText((String.valueOf(nutrientList.get(position).getFat())));
        holder.salt.setText((String.valueOf(nutrientList.get(position).getSalt())));
        holder.calorie.setText((String.valueOf(nutrientList.get(position).getCalorie())));
        holder.weightfood.setText((String.valueOf(nutrientList.get(position).getWeightfood())));



        return view;
    }

//    // Filter Class
//    public void filter(String charText) {
//        charText = charText.toLowerCase(Locale.getDefault());
//        nutrientList.clear();
//        if (charText.length() == 0) {
//            nutrientList.addAll(arraylist);
//        }
//        else
//        {
//            for (Nutrient nt : arraylist)
//            {
//                if (nt.getCountry().toLowerCase(Locale.getDefault()).contains(charText))
//                {
//                    nutrientList.add(nt);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }

}