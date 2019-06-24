package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListGroupAdapter extends BaseAdapter {
    Context context;
    ArrayList<ListGroup> ListGroups;
    TextView group_name_textView;
    TextView group_count;
    TextView group_goal;

    @Override
    public int getCount() {
        return this.ListGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return this.ListGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public ListGroupAdapter(Context context, ArrayList<ListGroup> ListGroups) {
        this.context = context;
        this.ListGroups = ListGroups;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        MyViewHolder holder = null;
        final int pos = i;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_grid, null);

            holder = new MyViewHolder();


            holder.title = (TextView) view.findViewById(R.id.title);
            holder.sub = (TextView) view.findViewById(R.id.sub);
            holder.line = (LinearLayout) view.findViewById(R.id.line);


            // At here set animation by their position.. Means if position = 0.. then animation start on textview with i*50 = 0 startOffset


            view.setTag(holder);

        } else {

            holder = (MyViewHolder) view.getTag();
        }

        ListGroup rowItem = (ListGroup) getItem(i);

        holder.title.setText(rowItem.getGroupname());
        holder.sub.setText(rowItem.getUserCount());


        return view;
    }


    class MyViewHolder {

        private TextView title;
        private TextView sub;
        private LinearLayout line;
        private TextView sem;

    }
    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_group, null);
            group_name_textView= convertView.findViewById(R.id.group_name_textView);
            group_count = convertView.findViewById(R.id.group_count);
            group_goal = convertView.findViewById(R.id.group_goal);
        }
        group_name_textView.setText(ListGroups.get(position).getGroupname());
        group_count.setText(String.valueOf(ListGroups.get(position).getUserCount()));
        group_goal.setText(ListGroups.get(position).getGoal());
        return convertView;
    }*/
}
