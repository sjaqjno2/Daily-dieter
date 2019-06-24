package com.example.doo.dailydieter;

        import android.app.Activity;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.text.InputType;
        import android.util.Log;
        import android.view.View;
        import android.view.inputmethod.EditorInfo;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.google.gson.JsonObject;

        import java.util.concurrent.TimeUnit;

        import okhttp3.OkHttpClient;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;
        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;

    public String correct_message;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences spref = getSharedPreferences("Cropped Image", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.clear();

        username = (EditText)findViewById(R.id.dietIDView);
        username.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        username.setInputType(InputType.TYPE_CLASS_TEXT);
        password = (EditText)findViewById(R.id.password);

        Button loginButton = (Button)findViewById(R.id.login);
        Button registerButton = (Button)findViewById(R.id.register);
        Button findButton = (Button)findViewById(R.id.find);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login(username.getText().toString(), password.getText().toString());
//                if(username.getText().toString().equals("user") &&
//                        password.getText().toString().equals("pass")  ) {
//                    Toast.makeText(LoginActivity.this,"ID and Password is correct",
//                            Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(LoginActivity.this,"ID and Password is not correct",
//                            Toast.LENGTH_SHORT).show();
//                }

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void login(String id, String password) {

        OkHttpClient OClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .client(OClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonObject> req = service.login(id, password);
        req.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject mList2 = response.body();
                Log.d("mList2", mList2.toString());
                JsonObject token_item = mList2.getAsJsonObject();
                String token = token_item.get("token").getAsString();
                Log.d("token", token);
                if(token.equals("Could not create token")) {
                    Toast.makeText(LoginActivity.this, "can not create token", Toast.LENGTH_LONG);
                }else if (token.equals("Not correct password")){
                    Toast.makeText(LoginActivity.this, "Not correct input variable", Toast.LENGTH_LONG);
                }else if(token.equals("There is no user")){
                    Toast.makeText(LoginActivity.this, "Not correct input variable", Toast.LENGTH_LONG);
                }else {
                    saveToken(token);
                    Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this,"error happened", Toast.LENGTH_SHORT).show();
                Log.d("error", String.valueOf(t));
            }
        });
    }
    private void saveToken(String token) {
        SharedPreferences pref = getSharedPreferences("Token", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        Log.d("토큰저장", "성공");
        editor.putString("token", token);
        editor.commit();
    }

}
