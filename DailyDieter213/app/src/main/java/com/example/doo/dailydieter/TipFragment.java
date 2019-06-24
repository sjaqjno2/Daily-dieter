package com.example.doo.dailydieter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TipFragment extends Fragment {
    public RecyclerView tipListView;
    ArrayList<list_item> user_list = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;
    public String title;
    public String content;
    public String id;
    public String date;
    public int count;

    private GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            //누르고 뗄 때 한번만 인식하도록 하기위해서
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    public static TipFragment newInstance() {
        TipFragment fragment = new TipFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tip, null);
        tipListView = view.findViewById(R.id.tipListView);
        EditText searchText = view.findViewById(R.id.searchTipText);

        tipListView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        tipListView.setLayoutManager(mLayoutManager);
        RecyclerView.OnItemTouchListener onItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //손으로 터치한 곳의 좌표를 토대로 해당 Item의 View를 가져옴
                View childView = rv.findChildViewUnder(e.getX(),e.getY());

                //터치한 곳의 View가 RecyclerView 안의 아이템이고 그 아이템의 View가 null이 아니라
                //정확한 Item의 View를 가져왔고, gestureDetector에서 한번만 누르면 true를 넘기게 구현했으니
                //한번만 눌려서 그 값이 true가 넘어왔다면
                if(childView != null && gestureDetector.onTouchEvent(e)){

                    //현재 터치된 곳의 position을 가져오고
                    int currentPosition = rv.getChildAdapterPosition(childView);

                    //해당 위치의 Data를 가져옴
                    String a = user_list.get(currentPosition).getTitle();
                    String b = user_list.get(currentPosition).getNickname();

                    Intent intent = new Intent(getActivity(), SelectedTipActivity.class);
                    intent.putExtra("title",a);
                    intent.putExtra("writerid",b);

                    tipCountUp(a);
                    getTipPost();
                    startActivity(intent);

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
        tipListView.addOnItemTouchListener(onItemTouchListener);
        getTipPost();

        FloatingActionButton searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchText.getText().toString();
                searchPost(search);
            }
        });

        return view;

    }

    public void getTipPost(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<JsonArray> a = service.getTipPost();
        a.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                user_list.clear();

                JsonArray mList = response.body();
                if(mList.size() != 0) {
                    for(JsonElement item : mList) {

                        JsonObject itemJson = item.getAsJsonObject();
                        title = itemJson.get("title").getAsString();
                        content = itemJson.get("content").getAsString();
                        id = itemJson.get("userid").getAsString();
                        date = itemJson.get("date").getAsString();
                        count = itemJson.get("count").getAsInt();
                        Log.d("title :    ", title);
                        list_item user = new list_item(title, content, id, date, count);
                        if (user.title != null) {
                            // Binds all strings into an array
                            user_list.add(user);
                        }

                        if (user_list.size() != 0) {
                            Log.d("user list : ", String.valueOf(user_list));
                            // Pass results to ListViewAdapter Class
                            ListPostAdapter adapter = new ListPostAdapter(user_list);

                            // Binds the Adapter to the ListView
                            tipListView.setAdapter(adapter);

                        }

                    }
                }


            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(getActivity(), "Error happened", Toast.LENGTH_LONG);
                Log.d("err", t.toString());
            }
        });
    }



    public void tipCountUp(String title) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonArray> req = service.tipCount(title);
        req.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    public void searchPost(String search) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonArray> req = service.searchTip(search);
        req.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                user_list.clear();
                JsonArray mList = response.body();
                if(!mList.isJsonNull() ){
                    for(JsonElement item : mList) {

                        JsonObject itemJson = item.getAsJsonObject();
                        title = itemJson.get("title").getAsString();
                        content = itemJson.get("content").getAsString();
                        id = itemJson.get("userid").getAsString();
                        date = itemJson.get("date").getAsString();
                        count = itemJson.get("count").getAsInt();
                        Log.d("title", title);
                        list_item user = new list_item(title, content, id, date, count);
                        if (user.title != null) {
                            // Binds all strings into an array
                            user_list.add(user);
                        }

                        if (user_list.size() != 0) {
                            Log.d("user list1", String.valueOf(user_list.size()));
                            Log.d("user_list2", user_list.get(user_list.size()-1).getContent());
                            Log.d("user_list3", user_list.get(user_list.size()-1).getTitle());
                            Log.d("user_list4", user_list.get(user_list.size()-1).getWrite_date());

                            // Pass results to ListViewAdapter Class
                            ListPostAdapter adapter2 = new ListPostAdapter(user_list);

                            // Binds the Adapter to the ListView
                            tipListView.setAdapter(adapter2);
                        }
                    }
                }else {

                }

            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(getActivity(), "Error happened", Toast.LENGTH_LONG);
                Log.d("err", t.toString());
            }
        });
    }

}
