package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

import static android.util.Log.d;

public class SelectedWishListActivity extends Activity {
    public Button update;
    public Button delete;
    public Button deleteWish;
    public TextView title_view;
    public TextView content_view;
    public String title;
    public String content;
    public String writer_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_wish_list);

        title_view = (TextView) findViewById(R.id.title);
        content_view = (TextView) findViewById(R.id.content);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        writer_id = intent.getStringExtra("writer_id");

        title_view.setText(title);
        content_view.setText(content);

        deleteWish = (Button) findViewById(R.id.deletewish);
        deleteWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWish(title, writer_id);
            }
        });
    }

    private String loadToken() {
        SharedPreferences pref = getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }

    public void deleteWish(String title, String writerid){
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

        Call<JsonObject> a = service.deleteWish(title, writerid);
        a.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(SelectedWishListActivity.this, "Delete success", Toast.LENGTH_LONG);
                Intent go_wish = new Intent(SelectedWishListActivity.this, WishListActivity.class);
                startActivity(go_wish);
                finish();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(SelectedWishListActivity.this, "Error happened", Toast.LENGTH_LONG);
                Log.d("err", t.toString());
            }
        });
    }
}