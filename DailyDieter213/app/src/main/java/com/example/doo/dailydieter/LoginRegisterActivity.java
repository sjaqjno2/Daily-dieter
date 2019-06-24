package com.example.doo.dailydieter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRegisterActivity extends AppCompatActivity {

    private EditText id;
    private EditText password;
    private EditText name;
    private EditText height;
    private EditText weight;
    private EditText phone;
    private EditText email;
    private String gender1;
    private boolean confirm;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        id = (EditText)findViewById(R.id.idText);
        password = (EditText)findViewById(R.id.passwordText);
        name = (EditText)findViewById(R.id.nameText);
        height = (EditText)findViewById(R.id.heightText);
        weight = (EditText)findViewById(R.id.weightText);
        phone = (EditText)findViewById(R.id.phoneText);
        email = (EditText)findViewById(R.id.emailText);
        RadioGroup gender = (RadioGroup) findViewById(R.id.radiogroup1);
        RadioButton male = (RadioButton)findViewById(R.id.male);
        RadioButton female = (RadioButton)findViewById(R.id.female);

        if(male.isChecked())
            gender1 = "male";

        else if(female.isChecked())
            gender1 = "female";

        Button registerButton = (Button)findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                String id1 = id.getText().toString();
                String password1 = password.getText().toString();
                String name1 = name.getText().toString();
                int height1 = Integer.parseInt(height.getText().toString());
                int weight1 = Integer.parseInt(weight.getText().toString());
                String phone1 = phone.getText().toString();
                String email1 = email.getText().toString();

                registerID(id1, password1, name1, height1, weight1, phone1, email1, gender1);

            }
        });
    }
    public void makeDialog(boolean confirm) {
        if (confirm==true){
            Log.d("Good", "gg");
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginRegisterActivity.this);
            AlertDialog OK = builder.create();
            OK.setTitle("등록 완료");
            OK.setMessage("회원가입이 완료 되었습니다.");
            builder.setPositiveButton("확인", (dialog, which) -> {
                Intent go_Login = new Intent(LoginRegisterActivity.this, LoginActivity.class);
                startActivity(go_Login);
            });
            OK.show();
        }else {
            //ToDo 실패를 확인했을 때 제대로 되는지 확인 할 것
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginRegisterActivity.this);
            AlertDialog noOK = builder.create();
            noOK.setTitle("등록 실패");
            noOK.setMessage("회원가입에 실패하셨습니다. 빈 칸이 없는지 확인해주세요");
            builder.setNegativeButton("확인", (dialog, which) -> {
                noOK.dismiss();
            });
            noOK.show();
        }
    }
    public void registerID(String id, String password, String name, int height, int weight, String phone,
                           String email, String gender) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonObject> req = service.getRegisterRES(id, password, name, height, weight, phone, email, gender);
        Log.d("해보자", "준성이와");
        req.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Test", response.body().toString());

                JsonObject mList = response.body();
                Log.d("protocol41", String.valueOf(mList.get("protocol41").getAsBoolean()));
                boolean boo = mList.get("protocol41").getAsBoolean();
                Log.d("boo :" , String.valueOf(boo));
                makeDialog(true);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("알아보자wwww" , "왜인지",t);
                makeDialog(false);

            }
        });
        Log.d("confirm :", String.valueOf(confirm));
        makeDialog(confirm);

    }
}

