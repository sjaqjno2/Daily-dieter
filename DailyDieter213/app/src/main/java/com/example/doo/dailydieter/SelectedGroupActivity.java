package com.example.doo.dailydieter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectedGroupActivity extends Activity {

    public String groupName;
    public TextView groupNameView;
    public TextView userCountView;
    public TextView goalView;

    public FloatingActionButton editGoal;
    public Button chat;
    public Button participate;
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.selected_group);

        Intent in3 = getIntent();
        groupName = in3.getStringExtra("groupName");
        Log.d("selected groupName", groupName);
        String userCount = in3.getStringExtra("userCount");

        //initialize the name of group
        SharedPreferences gpref = getSharedPreferences("Group", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = gpref.edit();
        editor.clear();
        editor.commit();

        checkParticipation(groupName);
        checkProducer(groupName);

        groupNameView = findViewById(R.id.groupNameView);
        groupNameView.setText(groupName);
        userCountView = findViewById(R.id.userCountView);
        userCountView.setText(userCount);

        participate = findViewById(R.id.participateButton);
        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participateGroup(groupName);
            }
        });

        chat = findViewById(R.id.chatButton);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save group name
                SharedPreferences gpref = getSharedPreferences("Group", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = gpref.edit();
                editor.clear();
                editor.putString("name", groupName);
                editor.commit();

                //Go chat
                Intent go_chat = new Intent(SelectedGroupActivity.this, ChatMainActivity.class);
                startActivity(go_chat);
            }
        });

        editGoal = findViewById(R.id.editGoalButton);
        editGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectedGroupActivity.this);
                final EditText et = new EditText(SelectedGroupActivity.this);
                builder.setView(et);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String group_goal = et.getText().toString();
                        changeGoal(groupName, group_goal);
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
                builder.setTitle("Change goal");
                builder.setMessage("Type group goal if you change");
                builder.show();
            }
        });

    }
    public void checkParticipation(String groupName){
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
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        final OkHttpClient[] client = {httpClient.build()};
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client[0])
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<JsonPrimitive> req = service.checkParticipation(groupName);
        req.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                JsonPrimitive mList = response.body();

                String result = mList.toString();
                Log.d("result", result);
                if (result.equals("\"Already_participateGroup\"")) {
                    chat.setVisibility(View.VISIBLE);
                    participate.setVisibility(View.INVISIBLE);

                } else if (result.equals("\"No_participation_user\"")) {
                    chat.setVisibility(View.INVISIBLE);
                    participate.setVisibility(View.VISIBLE);
                }else if( result.equals("\"error_happened\"")){
                    Toast.makeText(SelectedGroupActivity.this, "Error happened", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Toast.makeText(SelectedGroupActivity.this, "Check participation failure", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }

    public void checkProducer(String groupName){
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
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        final OkHttpClient[] client = {httpClient.build()};
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client[0])
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<JsonPrimitive> req = service.checkProducer(groupName);
        req.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                JsonPrimitive mList = response.body();

                String result = mList.toString();
                Log.d("result", result);
                if (result.equals("\"sameuser\"")) {
                    editGoal.setVisibility(View.VISIBLE);

                } else if (result.equals("\"not_sameuser\"")) {
                    editGoal.setVisibility(View.GONE);
                }else if( result.equals("\"error_happened\"")){
                    Toast.makeText(SelectedGroupActivity.this, "Error happened", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Toast.makeText(SelectedGroupActivity.this, "Check participation failure", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }

    public void participateGroup(String groupName){
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
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        final OkHttpClient[] client = {httpClient.build()};
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client[0])
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<JsonPrimitive> req = service.participateGroup(groupName);
        req.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                JsonPrimitive mList = response.body();

                String result = mList.toString();

                if(result.equals("\"participate_success\"")){
                    chat.setVisibility(View.VISIBLE);
                    participate.setVisibility(View.INVISIBLE);
                    userCountUp(groupName);
                }else if( result.equals("\"error_happened\"")){
                    Toast.makeText(SelectedGroupActivity.this, "Error happened", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Toast.makeText(SelectedGroupActivity.this, "Participation failure", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }

    public void userCountUp(String groupName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<JsonObject> req = service.userCountUp(groupName);
        req.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(SelectedGroupActivity.this, "Participation Success", Toast.LENGTH_SHORT).show();
                Intent go_main = new Intent(SelectedGroupActivity.this, MainActivity.class);
                go_main.putExtra("selectedFrag", "group");
                startActivity(go_main);
                finish();

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("err", t.toString());

            }
        });
    }
    private String loadToken() {
        SharedPreferences pref = getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }

    public void changeGoal(String group_name, String group_goal){
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

        Call<JsonObject> req = service.changeGoal(group_name, group_goal);
        req.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(SelectedGroupActivity.this, "Change group success", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(SelectedGroupActivity.this, "Change group fail", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }
}
