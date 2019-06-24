package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.util.Data;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WritingPostInsertingActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_community);

        Intent in = getIntent();
        String title = in.getStringExtra("title");
        String content = in.getStringExtra("content");
        insertPost(title,content);
//        sendImage();

        Intent go_main = new Intent(WritingPostInsertingActivity.this, MainActivity.class);
        go_main.putExtra("selectedFrag", "community");
        startActivity(go_main);
        finish();
    }

    public void insertPost(String title, String content){
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

        Call<JsonObject> req = service.getWritingRES(title, content);
        req.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(WritingPostInsertingActivity.this, "Insertion success", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(WritingPostInsertingActivity.this, "Insertion failure", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }

    private String getRealPathFromURI(Uri contentURI) {
        String filePath; Cursor cursor = this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            filePath = contentURI.getPath();
        } else {
            cursor.moveToFirst(); int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(idx); cursor.close();
        }
        return filePath;
    }

    private void sendImage() {
        Intent intent = getIntent();
        String a = intent.getStringExtra("image");
        Uri uri = Uri.parse(a);

        File imgFile = new File(getRealPathFromURI(uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/png"), imgFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imgFile.getName(), requestFile);
        RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, "id value");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService apiRequest = retrofit.create(RetrofitService.class);
        apiRequest.sendImage(id, body).enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {

            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }

        });
    }
    private String loadToken() {
        SharedPreferences pref = getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }

}