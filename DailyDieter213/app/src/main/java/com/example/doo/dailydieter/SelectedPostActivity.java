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

public class SelectedPostActivity extends Activity {
    public Button update;
    public Button delete;
    public Button wish;
    public TextView title_view;
    public TextView content_view;
    public String original_title;
    public String original_content;
    public String writerid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_post);

        title_view = (TextView) findViewById(R.id.title);
        content_view = (TextView) findViewById(R.id.content);

        Intent intent = getIntent();
        writerid = intent.getStringExtra("writerid");
        original_title = intent.getStringExtra("title");
        original_content = intent.getStringExtra("content");



        confirmWriter(original_title, original_content);

        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(original_title,original_content);

                Intent go_main = new Intent(SelectedPostActivity.this, MainActivity.class);
                startActivity(go_main);
            }
        });

        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SelectedPostActivity.this, UpdatePostActivity.class);
                intent.putExtra("new_title", title_view.getText());
                intent.putExtra("new_content", content_view.getText());
                intent.putExtra("original_title",original_title);
                intent.putExtra("original_content",original_content);
                startActivity(intent);
            }
        });
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

                Call<JsonObject> req = service.saveWishlist(writerid, original_title, "community");
                req.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Toast.makeText(SelectedPostActivity.this.getApplicationContext(), "Post is saved in wishlist", Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(SelectedPostActivity.this.getApplicationContext(), "Error happened", Toast.LENGTH_SHORT).show();
                        Log.d("err",t.toString());
                    }
                });

            }
        });
    }
    public void confirmWriter(String title, String content) {
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

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonPrimitive> req = service.confirmWriter(title, content);
        req.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                JsonPrimitive mList = response.body();
                String result = mList.toString();
                if(result.equals("\"same_user\"")){
                    Log.d("들어옴","1)");
                    delete.setVisibility(View.VISIBLE);   // 화면에보임
                    update.setVisibility(View.VISIBLE);  // 화면에 안보임

                }else if(result.equals("\"not_same_user\"")){
                    delete.setVisibility(View.INVISIBLE);   // 화면에보임
                    update.setVisibility(View.INVISIBLE);  // 화면에 안보임
                }
                title_view.setText(original_title);
                content_view.setText(original_content);

            }
            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                d("err", t.toString());
            }
        });
    }
    public void deletePost(String title, String content) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonArray> req = service.deletePost(title, content);
        req.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                //TODO: 삭제시 어디로 가야할지 명시
                Intent go_main = new Intent(SelectedPostActivity.this, MainActivity.class);
                go_main.putExtra("selectedFrag", "community");
                startActivity(go_main);
                finish();
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }
    private String loadToken() {
        SharedPreferences pref = getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }
}
