package com.example.doo.dailydieter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapterFoodRecognition extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<FoodRecognition> foodRecognitionList = null;
    private ArrayList<FoodRecognition> arraylist;

    public ListViewAdapterFoodRecognition(Context context, List<FoodRecognition> foodRecognitionList) {
        mContext = context;
        this.foodRecognitionList = foodRecognitionList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<FoodRecognition>();
        this.arraylist.addAll(foodRecognitionList);
    }

    public class ViewHolder {
        TextView namefood;
        TextView score;
    }

    public int getCount() {
        return foodRecognitionList.size();
    }

    public FoodRecognition getItem(int position) {
        return foodRecognitionList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ListViewAdapterFoodRecognition.ViewHolder holder;
        if (view == null) {
            holder = new ListViewAdapterFoodRecognition.ViewHolder();
            view = inflater.inflate(R.layout.listview_foodrecognition, null);
            // Locate the TextViews in listview_item_diet_process.xml
            holder.namefood = (TextView) view.findViewById(R.id.namefood);
            holder.score = (TextView) view.findViewById(R.id.score);
            view.setTag(holder);
        } else {
            holder = (ListViewAdapterFoodRecognition.ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.namefood.setText(foodRecognitionList.get(position).getNamefood());
//        holder.score.setText(String.valueOf(foodRecognitionList.get(position).getScore()));
        holder.score.setText(String.valueOf(  (int) Math.round( foodRecognitionList.get(position).getScore()/0.01 ) ) );



        return view;
    }
}
