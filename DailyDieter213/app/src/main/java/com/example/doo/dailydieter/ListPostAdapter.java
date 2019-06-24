package com.example.doo.dailydieter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;

    public static class PostViewHodler extends RecyclerView.ViewHolder {
        TextView nickname_View;
        TextView date_textView;
        TextView title_textView;
        TextView count_textView;
        PostViewHodler(View view) {
            super(view);
            nickname_View = view.findViewById(R.id.nickname_textview);
            date_textView = view.findViewById(R.id.date_textview);
            title_textView  =view.findViewById(R.id.title_textview);
            count_textView = view.findViewById(R.id.count);
        }
    }

    private ArrayList<list_item> list_itemArrayList;
    ListPostAdapter(ArrayList<list_item> list_itemArrayList) {
        this.list_itemArrayList =list_itemArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_post, parent, false);
        return new PostViewHodler(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        PostViewHodler postHolder = (PostViewHodler) holder;

        postHolder.nickname_View.setText(list_itemArrayList.get(position).getNickname());
        postHolder.date_textView.setText(list_itemArrayList.get(position).getWrite_date());
        postHolder.title_textView.setText(list_itemArrayList.get(position).getTitle());
        String result = list_itemArrayList.get(position).getCount().toString() + "hits";
        postHolder.count_textView.setText(result);
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list_itemArrayList.size();
    }


}