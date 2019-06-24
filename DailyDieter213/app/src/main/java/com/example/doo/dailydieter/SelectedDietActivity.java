package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.JsonArray;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.util.Log.d;

import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

public class SelectedDietActivity extends Activity {
    private TextView id_view;
    private   TextView namefood1_view;
    private   TextView namefood2_view;
    private   TextView namefood3_view;
    private  TextView namefood4_view;
    private   TextView namefood5_view;
    private    TextView carb_gram_view;
    private   TextView protein_gram_view;
    private  TextView fat_gram_view;
    private    TextView salt_gram_view;
    private  TextView food1_calorie_view;
    private  TextView food2_calorie_view;
    private   TextView food3_calorie_view;
    private   TextView food4_calorie_view;
    private   TextView food5_calorie_view;
    private   TextView mealtime_view;
    private   TextView date_view;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_diet);

        id_view = findViewById(R.id.id_view);
        namefood1_view = findViewById(R.id.food1);
        namefood2_view = findViewById(R.id.food2);
        namefood3_view = findViewById(R.id.food3);
        namefood4_view = findViewById(R.id.food4);
        namefood5_view = findViewById(R.id.food5);
        carb_gram_view = findViewById(R.id.carb_gram);
        protein_gram_view = findViewById(R.id.protein_gram);
        fat_gram_view = findViewById(R.id.fat_gram);
        salt_gram_view = findViewById(R.id.salt_gram);
        food1_calorie_view = findViewById(R.id.food1_calorie);
        food2_calorie_view = findViewById(R.id.food2_calorie);
        food3_calorie_view = findViewById(R.id.food3_calorie);
        food4_calorie_view = findViewById(R.id.food4_calorie);
        food5_calorie_view = findViewById(R.id.food5_calorie);
        mealtime_view = findViewById(R.id.mealtimeView);
        date_view = findViewById(R.id.dateView);

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

        id_view.setText(userid);
        namefood1_view.setText(namefood1);
        namefood2_view.setText(namefood2);
        namefood3_view.setText(namefood3);
        namefood4_view.setText(namefood4);
        namefood5_view.setText(namefood5);
        food1_calorie_view.setText(String.valueOf(calorie1)+ "cal");
        food2_calorie_view.setText(String.valueOf(calorie2)+ "cal");
        food3_calorie_view.setText(String.valueOf(calorie3)+ "cal");
        food4_calorie_view.setText(String.valueOf(calorie4)+ "cal");
        food5_calorie_view.setText(String.valueOf(calorie5)+ "cal");
        carb_gram_view.setText(String.valueOf(sum_carb));
        protein_gram_view.setText(String.valueOf(sum_prot));
        fat_gram_view.setText(String.valueOf(sum_fat));
        salt_gram_view.setText(String.valueOf(sum_salt));
        mealtime_view.setText(mealtime);
        date_view.setText(date);

        Log.d("set",userid+mealtime+date);

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
                Intent go_update_diet = new Intent(SelectedDietActivity.this, UpdateDietActivity.class);
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
                Intent go_main = new Intent(SelectedDietActivity.this, MainActivity.class);
                go_main.putExtra("selectedFrag", "diet");
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