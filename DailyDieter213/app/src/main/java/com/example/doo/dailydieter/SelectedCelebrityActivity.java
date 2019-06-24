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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectedCelebrityActivity extends Activity {
    public TextView celebrityTitleTextView;
    public TextView celebrityContentTextView;
    Button wish;

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.selected_celebrity);

        celebrityTitleTextView = findViewById(R.id.celebrityTitle);
        celebrityContentTextView = findViewById(R.id.celebrityContent);

        Intent in3 = getIntent();
        String title = in3.getStringExtra("title");
        String writerid = in3.getStringExtra("writerid");
        getCelebrityFromSelection(title);

        wish = (Button) findViewById(R.id.wish);
        wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Call<JsonObject> req = service.saveWishlist(writerid, title, "celebrity");
                req.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Toast.makeText(SelectedCelebrityActivity.this.getApplicationContext(), "Celebrity tip is saved in wishlist", Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(SelectedCelebrityActivity.this.getApplicationContext(), "Celebrity tip is saved in wishlist", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
    }


    public void getCelebrityFromSelection(String title) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);

        Call<JsonArray> req = service.getCelebrityFromSelection(title);
        req.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JsonArray mList = response.body();
                for (JsonElement item : mList) {
                    JsonObject itemJson = item.getAsJsonObject();

                    String title = itemJson.get("title").getAsString();
                    String content = itemJson.get("content").getAsString();

                    celebrityTitleTextView.setText(title);
                    celebrityContentTextView.setText(content);
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(SelectedCelebrityActivity.this, "Error Happened", Toast.LENGTH_LONG);
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