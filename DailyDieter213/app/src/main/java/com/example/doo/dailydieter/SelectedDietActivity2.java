package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectedDietActivity2 extends AppCompatActivity{

    public FitChart saltChart;
    public FitChart caloireChart;

    public int weight;
    public int height;
    public ListView listDietView;
    public String userid;
    public String sex;
    public float standardH;
    public float standardW;
    public float standadrC;

    public TextView idTextView;
    public TextView heightTextView;

    public TextView percentOfSalt;
    public TextView percentOfCalorie;

    public ArrayList<ListSelectedDiet> listDiets;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.selected_diet_2);

        idTextView =findViewById(R.id.idDietTextView);
        heightTextView = findViewById(R.id.heightDietTextView);
        listDietView = findViewById(R.id.listDiet);
        saltChart = (FitChart)findViewById(R.id.big1);
        caloireChart = (FitChart)findViewById(R.id.big2);
        percentOfCalorie = findViewById(R.id.big2_text);
        percentOfSalt= findViewById(R.id.big1_text);

        Intent fromDietFrag = getIntent();
        int id = fromDietFrag.getIntExtra("id",99999999);
        String userid = fromDietFrag.getStringExtra("user");
        String mealtime = fromDietFrag.getStringExtra("mealtime");
        String date = fromDietFrag.getStringExtra("date");

        int eaten_weight1 = fromDietFrag.getIntExtra("eaten_weight1", 1);
        int eaten_weight2 = fromDietFrag.getIntExtra("eaten_weight2", 1);
        int eaten_weight3 = fromDietFrag.getIntExtra("eaten_weight3", 1);
        int eaten_weight4 = fromDietFrag.getIntExtra("eaten_weight4", 1);
        int eaten_weight5 = fromDietFrag.getIntExtra("eaten_weight5", 1);
        float sum_carb = fromDietFrag.getFloatExtra("sum_carb", 1f);
        float sum_prot = fromDietFrag.getFloatExtra("sum_prot", 1f);
        float sum_fat = fromDietFrag.getFloatExtra("sum_fat", 1f);
        float sum_salt = fromDietFrag.getFloatExtra("sum_salt", 1f);
        String namefood1 = fromDietFrag.getStringExtra("namefood1");
        String namefood2 = fromDietFrag.getStringExtra("namefood2");
        String namefood3 = fromDietFrag.getStringExtra("namefood3");
        String namefood4 = fromDietFrag.getStringExtra("namefood4");
        String namefood5 = fromDietFrag.getStringExtra("namefood5");
        float calorie1 = fromDietFrag.getFloatExtra("calorie1", 1f);
        float calorie2 = fromDietFrag.getFloatExtra("calorie2", 1f);
        float calorie3 = fromDietFrag.getFloatExtra("calorie3", 1f);
        float calorie4 = fromDietFrag.getFloatExtra("calorie4", 1f);
        float calorie5 = fromDietFrag.getFloatExtra("calorie5", 1f);

        //user에 따른 표중 체중, 칼로리 셋
        getUserInfo();

        //id를 사용하여 식단 저장
        getDietFromId(id);

        Button deleteDiet = (Button)findViewById(R.id.deleteDietButton);
        deleteDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDiet(id, userid, mealtime, date);
            }
        });

        Button updateDiet = (Button)findViewById(R.id.updateDietButton);
        updateDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_update_diet = new Intent(SelectedDietActivity2.this, UpdateDietActivity.class);
                go_update_diet.putExtra("id",id);
                go_update_diet.putExtra("userid",userid);
                go_update_diet.putExtra("mealtime", mealtime);
                go_update_diet.putExtra("date", date);

                go_update_diet.putExtra("namefood1",namefood1);
                go_update_diet.putExtra("namefood2",namefood2);
                go_update_diet.putExtra("namefood3",namefood3);
                go_update_diet.putExtra("namefood4",namefood4);
                go_update_diet.putExtra("namefood5",namefood5);

                go_update_diet.putExtra("eaten_weight1", eaten_weight1);
                go_update_diet.putExtra("eaten_weight2", eaten_weight2);
                go_update_diet.putExtra("eaten_weight3", eaten_weight3);
                go_update_diet.putExtra("eaten_weight4", eaten_weight4);
                go_update_diet.putExtra("eaten_weight5", eaten_weight5);

                go_update_diet.putExtra("sum_carb", sum_carb);
                go_update_diet.putExtra("sum_prot", sum_prot);
                go_update_diet.putExtra("sum_fat", sum_fat);
                go_update_diet.putExtra("sum_salt", sum_salt);
                startActivity(go_update_diet);
            }
        });




    }

    public void deleteDiet(int id, String userid, String mealtime, String date) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonArray> req = service.deleteDiet(id, userid, mealtime, date);
        req.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                //TODO: 삭제시 어디로 가야할지 명시
                Intent go_main = new Intent(SelectedDietActivity2.this, MainActivity.class);
                go_main.putExtra("selectedFrag", "diet");
                startActivity(go_main);
                finish();
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    public float standardWeight(int height, String sex) {
        float weight = 0f;
        if (sex.equals("male")) {
            weight = height/100*height/100*22;

        } else if (sex.equals("female")) {
            weight = height/100*height/100*21;

        }

        return weight;
    }

    public float standardCalorie (float weight) {
        float calorie = weight * 35;
        return calorie;
    }

    //유저 정보 가져오기
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
                JsonArray mList = response.body();
                Log.d("mList", mList.toString());
                for (JsonElement item : mList) {
                    JsonObject itemJson = item.getAsJsonObject();
                    userid = itemJson.get("id").getAsString();
                    height = itemJson.get("height").getAsInt();
                    weight = itemJson.get("weight").getAsInt();
                    sex = itemJson.get("gender").getAsString();

                    Log.d("userid", userid);
                    String id_result = userid +"'s diet ";
                    idTextView.setText(id_result);


                    standardW = standardWeight(height, sex);
                    standadrC = standardCalorie(standardW);

                    String result = "Standard Calorie : " + String.valueOf(standadrC) +"kCal";
                    heightTextView.setText(result);
                }

                saltChart.setMinValue(0);
                saltChart.setMaxValue(2000);

                Log.d("standardC", String.valueOf(standadrC));
                Log.d("standardC int", String.valueOf((int)standadrC));
                caloireChart.setMinValue(0);
                caloireChart.setMaxValue((int)standadrC);


            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(SelectedDietActivity2.this, "Getting user info failure", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }

    public void getDietFromId(int id){
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

        Call<JsonArray> req = service.getDietFromId(id);
        req.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listDiets = new ArrayList<>();
                JsonArray mList = response.body();
                for (JsonElement item : mList) {
                    JsonObject itemJson = item.getAsJsonObject();

                    String namefood1 = itemJson.get("namefood1").getAsString();
                    String namefood2 = itemJson.get("namefood2").getAsString();
                    String namefood3 = itemJson.get("namefood3").getAsString();
                    String namefood4 = itemJson.get("namefood4").getAsString();
                    String namefood5 = itemJson.get("namefood5").getAsString();
                    String[] nameList = {namefood1, namefood2, namefood3, namefood4, namefood5};

                    float calorie1 = itemJson.get("calorie1").getAsFloat();
                    float calorie2 = itemJson.get("calorie2").getAsFloat();
                    float calorie3 = itemJson.get("calorie3").getAsFloat();
                    float calorie4 = itemJson.get("calorie4").getAsFloat();
                    float calorie5 = itemJson.get("calorie5").getAsFloat();
                    float[] calorieList = {calorie1, calorie2, calorie3, calorie4, calorie5};

                    for(int i=0; i<nameList.length; i++) {
                        ListSelectedDiet wp = new ListSelectedDiet(nameList[i], calorieList[i]);
                        listDiets.add(wp);
                    }
                    ListSelectedDietAdapter adapter = new ListSelectedDietAdapter(SelectedDietActivity2.this, listDiets);
                    listDietView.setAdapter(adapter);

                    //현재 칼로리
                    float sumC = sumCalorie(calorieList);
                    float sum_salt = itemJson.get("sum_salt").getAsFloat();

                    Collection<FitChartValue> values_salt = new ArrayList<>();
                    values_salt.add(new FitChartValue(sum_salt, Color.parseColor("#f6b45e")));

                    Collection<FitChartValue> values_calorie = new ArrayList<>();
                    values_calorie.add(new FitChartValue(sumC, Color.parseColor("#f6b45e")));

                    saltChart.setValues(values_salt);
                    String result1 = String.valueOf((int)sum_salt/2000*100) +"%";
                    percentOfSalt.setText(result1);

                    caloireChart.setValues(values_calorie);
                    String result2 = String.valueOf((int)(sumC/standadrC)*100) + "%";
                    percentOfCalorie.setText(result2);
                }


            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(SelectedDietActivity2.this, "Getting user info failure", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());

            }
        });
    }

    private String loadToken() {
        SharedPreferences pref = getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }

    private float sumCalorie(float[] fa) {
        float sumC = 0f;
        for(int i=0; i<fa.length; i++) {
            sumC += fa[i];
        }
        return sumC;
    }
}
