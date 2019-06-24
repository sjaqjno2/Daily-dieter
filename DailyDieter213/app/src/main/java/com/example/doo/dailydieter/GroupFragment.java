package com.example.doo.dailydieter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupFragment extends Fragment {
    public GridView groupList;
    public ArrayList<ListGroup> listGroups = new ArrayList<>();
    public String id;
    public String groupName;
    public String goal;
    public String userCount;
    public GroupFragment() {

    }
    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, null);
        groupList = (GridView) view.findViewById(R.id.gridList);

        getGroup();
        selected();

        FloatingActionButton makeGroup = view.findViewById(R.id.makeGroupButton);
        makeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText et = new EditText(getActivity());
                final EditText et2 = new EditText(getActivity());

                builder.setView(et);
                builder.setView(et2);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("et", et.toString());
                        String group_name = et.getText().toString();
                        Log.d("groupName1", group_name);
                        String group_goal = et2.getText().toString();

                        makeGroup(group_name, group_goal);

                        participateGroup(group_name);
                        dialog.cancel();
                        //TODO onDestroy()의 결과가 어떻게 되는지
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        return;
                    }
                });
                builder.setTitle("Make group");
                builder.setMessage("Type group name if you make");
                builder.show();
            }
        });
        return view;
    }

    public void selected() {

        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                String a = listGroups.get(position).getGroupname();
                String b = listGroups.get(position).getGoal();
                String c = listGroups.get(position).getUserCount();

                Intent intent = new Intent(getActivity(), SelectedGroupActivity.class);
                intent.putExtra("groupName",a);
                intent.putExtra("userCount",c);

                startActivity(intent);

            }
        });
    }
    public void getGroup(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<JsonArray> a = service.getGroup();
        a.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listGroups.clear();

                JsonArray mList = response.body();
                for(JsonElement item : mList) {
                    ListGroup group;
                    JsonObject itemJson = item.getAsJsonObject();
                    groupName = itemJson.get("groupname").getAsString();
                    userCount = itemJson.get("usercount").getAsString();
                    Log.d("groupName :    ", groupName);
                    group = new ListGroup(groupName,userCount);

                    if(group.groupName != null) {
                        listGroups.add(group);
                    }

                    if (listGroups.size() != 0) {
                        Log.d("user list : ", String.valueOf(listGroups));
                        // Pass results to ListViewAdapter Class
                        ListGroupAdapter adapter = new ListGroupAdapter(getActivity(), listGroups);

                        // Binds the Adapter to the ListView
                        groupList.setAdapter(adapter);

                    }

//                    list_itemArrayList.add(new list_item(R.mipmap.ic_launcher, "AAA", title, new Date(System.currentTimeMillis()), content));
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(getActivity(), "Error happened", Toast.LENGTH_LONG);
                Log.d("err", t.toString());
            }
        });
    }

    public void makeGroup(String group_name, String group_goal){
        String token = loadToken();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain
                        .request()
                        .newBuilder()
                        .addHeader("authorization", token)
                        .build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<JsonObject> req = service.makeGroup(group_name, group_goal);
        req.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(getActivity(), "Make group success", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), "Make group fail", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }

    public void participateGroup(String group_name){
        String token = loadToken();
        Log.d("토큰불러오기", token);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain
                        .request()
                        .newBuilder()
                        .addHeader("authorization", token)
                        .build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<JsonPrimitive> req = service.participateGroup(group_name);
        req.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                Toast.makeText(getActivity(), "Participation success", Toast.LENGTH_SHORT).show();
                Intent go_main = new Intent(getActivity(), MainActivity.class);
                go_main.putExtra("selectedFrag", "group");
                startActivity(go_main);
                onDestroy();
            }
            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Toast.makeText(getActivity(), "Participation failure", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }

    private String loadToken() {
        SharedPreferences pref = getActivity().getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }
}