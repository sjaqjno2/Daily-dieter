package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdatePostActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_post);

        EditText title = (EditText) findViewById(R.id.title);
        EditText content = (EditText) findViewById(R.id.content);

        Intent intent = getIntent();
        title.setText(intent.getStringExtra("new_title"));
        content.setText(intent.getStringExtra("new_content"));
        String save1 = intent.getStringExtra("original_title");
        String save2 = intent.getStringExtra("original_content");



        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String a = title.getText().toString();
                String b = content.getText().toString();
                getData(a, b, save1, save2);

                Intent go_main = new Intent(UpdatePostActivity.this, MainActivity.class);
                startActivity(go_main);
            }
        });
    }

    public void getData(String new_title, String new_content, String original_title, String original_content){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonObject> req = service.updatePost(new_title, new_content, original_title, original_content);
        req.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(UpdatePostActivity.this,"Update success", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("err", t.toString());
                Toast.makeText(UpdatePostActivity.this,"Update failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
