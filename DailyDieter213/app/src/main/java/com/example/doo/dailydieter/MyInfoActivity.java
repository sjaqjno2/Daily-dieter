package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

public class MyInfoActivity extends Activity {

    private String id;
    private String name;
    private int height;
    private int weight;
    private String phone;
    private String email;
    private String gender;

    public ArrayList<ListInfo> listInfos;

    public ListView listInfoView;

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_my_info);
        listInfoView = findViewById(R.id.listInfoView);
        getUserInfo();
    }

    public void getUserInfo(){
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

        Call<JsonArray> req = service.getUserInfo();
        req.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listInfos = new ArrayList<>();
                listInfos.clear();
                JsonArray mList = response.body();
                for (JsonElement item : mList) {
                    JsonObject itemJson = item.getAsJsonObject();

                    id = itemJson.get("id").getAsString();
                    name = itemJson.get("name").getAsString();
                    height = itemJson.get("height").getAsInt();
                    weight = itemJson.get("weight").getAsInt();
                    phone = itemJson.get("phone").getAsString();
                    email = itemJson.get("email").getAsString();
                    gender = itemJson.get("gender").getAsString();

                    String[] nameList = {"id", "name", "height", "weight", "phone", "email", "gender"};
                    String[] valueList = {id, name, String.valueOf(height), String.valueOf(weight), phone, email, gender};

                    for(int i=0; i<nameList.length; i++) {
                        ListInfo li = new ListInfo(nameList[i], valueList[i]);
                        listInfos.add(li);
                    }

                    Log.d("listInfos", listInfos.get(2).getCategory());

                    ListInfoAdapter adapter = new ListInfoAdapter(MyInfoActivity.this, listInfos);
                    listInfoView.setAdapter(adapter);

                }


            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(MyInfoActivity.this, "Getting user info failure", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }
    private String loadToken() {
        SharedPreferences pref = getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }
}