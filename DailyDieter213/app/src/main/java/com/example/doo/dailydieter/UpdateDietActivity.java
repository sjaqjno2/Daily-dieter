package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONException;
import org.json.JSONObject;

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

public class UpdateDietActivity extends AppCompatActivity{

    public TextView mealTimeView;
    public TextView dateTimeView;

    public EditText carbohydrateEditText;
    public EditText proteinEditText;
    public EditText fatEditText;
    public EditText saltEditText;

    public ListView listFoodView;
    public Button nextButton, secondButton, updateButton;

    public TextView noticeView;

    public ArrayList<ListUpdateDiet> listUpdateDiets;
    public int[] eatenList;

    public ArrayList<Nutrient> wp = new ArrayList<>();

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.update_diet);

        //intent로 mealtime, date, id 받아오기
        Intent fromDietFrag = getIntent();

        int id = fromDietFrag.getIntExtra("id",999999999);
        String mealtime = fromDietFrag.getStringExtra("mealtime");
        String date = fromDietFrag.getStringExtra("date");

        //View 선언
        mealTimeView = findViewById(R.id.update_diet_mealtimeView);
        dateTimeView = findViewById(R.id.update_diet_dateView);
        listFoodView = findViewById(R.id.updateDietFoodList);
        carbohydrateEditText = findViewById(R.id.updateCarbEditText);
        proteinEditText = findViewById(R.id.updateProEditText);
        fatEditText = findViewById(R.id.updateFatEditText);
        saltEditText = findViewById(R.id.updateSaltEditText);
        noticeView = findViewById(R.id.noticeTextView);

        //View 텍스트 설정
        mealTimeView.setText(mealtime);
        dateTimeView.setText(date);

        //ListView Set, 탄단지 set
        getDiet(id);
        String result = "Update Diet Step 1." + "\n" + "Change name of food if you want to change." + "\n" + "If not, push nextButton";
        noticeView.setText(result);

        nextButton = findViewById(R.id.updateDietButton);
        secondButton = findViewById(R.id.secondButton);
        updateButton = findViewById(R.id.updateButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText의 값 가져오기
                String[] nameList = new String[5];
                for(int i =0; i<listUpdateDiets.size(); i++) {
                    EditText namefood = listFoodView.getChildAt(i).findViewById(R.id.updateFoodNameView);
                    nameList[i] = namefood.getText().toString();
                }
                Log.d("namefood", nameList[1]);
                //EditText값에 맞춰서 영양소 가져오기
                getData(nameList);

//                try {
//                    Log.d("thread sleep", "start");
//                    Thread.sleep(1000);
//                    Log.d("thread wake", "end");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                //Nutrient에 1g을 기준으로 영양소들이 다 들어가있다. 일단 영양소를 먹은 양에 따라 맞춰 변환
                //getData 안에 넣어놈

            }
        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //칼로리 변경 못하게 막고 탄단지 변하게 하고
                for(int i =0; i<listUpdateDiets.size(); i++) {
                    EditText foodCalorie = listFoodView.getChildAt(i).findViewById(R.id.updateFoodCalorieVeiw);
                    disableEditText(foodCalorie);
                }
                enableEditText(carbohydrateEditText);
                enableEditText(proteinEditText);
                enableEditText(saltEditText);
                enableEditText(fatEditText);

                secondButton.setVisibility(View.GONE);
                updateButton.setVisibility(View.VISIBLE);
                String result = "Finish step 1 & 2. Change the nutrient if you want" + "\n" + "Push 'Update' Button after finish";
                noticeView.setText(result);

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                String[] nameList = new String[5];
                float[] calorieList = new float[5];
                for(int i =0; i<listUpdateDiets.size(); i++) {
                    EditText foodCalorie = listFoodView.getChildAt(i).findViewById(R.id.updateFoodCalorieVeiw);
                    EditText foodName = listFoodView.getChildAt(i).findViewById(R.id.updateFoodNameView);
                    nameList[i] = foodName.getText().toString();
                    float calorie = (float)Double.parseDouble(foodCalorie.getText().toString());
                    calorieList[i] = calorie;
                }
                float carb = (float) Double.parseDouble(carbohydrateEditText.getText().toString());
                float prot = (float) Double.parseDouble(proteinEditText.getText().toString());
                float fat = (float) Double.parseDouble(fatEditText.getText().toString());
                float salt = (float) Double.parseDouble(saltEditText.getText().toString());

                //namefood, calorie, nutrient 사용해서 저장
                updateDiet(id, nameList, calorieList, carb, prot, fat, salt);
            }
        });

    }
    // id를 사용해서 하자
    //업데이트 식단
    public void updateDiet(int id, String[] nameList, float[] calorieList,
                           Float sum_carb, Float sum_prot,
                           Float sum_fat, Float sum_salt) {
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

        Call<JsonPrimitive> req = service.updateDiet(id, nameList[0], calorieList[0], nameList[1], calorieList[1],
                nameList[2], calorieList[2], nameList[3], calorieList[3], nameList[4], calorieList[4], sum_carb, sum_prot, sum_fat, sum_salt);
        req.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                Toast.makeText(UpdateDietActivity.this, "Update success", Toast.LENGTH_SHORT).show();
                JsonPrimitive mList = response.body();
                String result = mList.toString();
                if(result.equals("\"update_success\"")){
                    Intent go_main = new Intent(UpdateDietActivity.this, MainActivity.class);
                    go_main.putExtra("selectedFrag", "diet");
                    startActivity(go_main);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Toast.makeText(UpdateDietActivity.this, "error happened", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());
            }
        });

    }

    //Disable EditText
    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    //En EditText
    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }
    //FoodName 변경

    //확인을 누르면 칼로리가 변경 되도록

    //getDiet
    public void getDiet(int id){
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

        Call<JsonArray> a = service.getDietFromId(id);
        a.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listUpdateDiets = new ArrayList<>();
                listUpdateDiets.clear();

                JsonArray mList = response.body();
                Log.d("mList", mList.toString());
                for(JsonElement item : mList) {

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

                    float sum_carb = itemJson.get("sum_carbohydrate").getAsFloat();
                    float sum_prot = itemJson.get("sum_protein").getAsFloat();
                    float sum_fat = itemJson.get("sum_fat").getAsFloat();
                    float sum_salt = itemJson.get("sum_salt").getAsFloat();
                    carbohydrateEditText.setHint(String.valueOf(sum_carb));
                    carbohydrateEditText.setText(String.valueOf(sum_carb));
                    proteinEditText.setHint(String.valueOf(sum_prot));
                    proteinEditText.setText(String.valueOf(sum_prot));
                    fatEditText.setHint(String.valueOf(sum_fat));
                    fatEditText.setText(String.valueOf(sum_fat));
                    saltEditText.setHint(String.valueOf(sum_salt));
                    saltEditText.setText(String.valueOf(sum_salt));


                    int eaten_weight1 = itemJson.get("foodweight1").getAsInt();
                    int eaten_weight2 = itemJson.get("foodweight2").getAsInt();
                    int eaten_weight3 = itemJson.get("foodweight3").getAsInt();
                    int eaten_weight4 = itemJson.get("foodweight4").getAsInt();
                    int eaten_weight5 = itemJson.get("foodweight5").getAsInt();
                    eatenList = new int[] {eaten_weight1, eaten_weight2, eaten_weight3, eaten_weight4, eaten_weight5};

                    for(int i=0; i<nameList.length; i++) {
                        ListUpdateDiet diet = new ListUpdateDiet(nameList[i], calorieList[i]);

                        if(diet.namefood != null) {
                            listUpdateDiets.add(diet);
                        }
                    }

                    ListUpdateDietAdapter adapter = new ListUpdateDietAdapter(UpdateDietActivity.this, listUpdateDiets);
                    listFoodView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(UpdateDietActivity.this, "error happened when getting diet", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());
            }
        });
    }
    private String loadToken() {
        SharedPreferences pref = this.getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }


    public void getData(String[] nameList) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonArray> req = service.getNutrientResFromArray(nameList[0], nameList[1], nameList[2], nameList[3], nameList[4]);
        req.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                JsonArray mList = response.body();
                Log.d("mList in update", mList.toString());
                for (JsonElement item : mList) {
                    int i = 0;
                    JsonArray itemJson = item.getAsJsonArray();
                    String jsonString = itemJson.toString();
                    String jsonChange = jsonString.substring(1, jsonString.length()-1);
                    Log.d("json", jsonChange);


                    JSONObject itemJsonObject = null;
                    try {
                        itemJsonObject = new JSONObject(jsonChange);
                        Log.d("JsonObject", itemJsonObject.toString());
                        float weightfood = (float)Double.parseDouble(itemJsonObject.get("weightfood").toString());
                        float protein = (float) Double.parseDouble(itemJsonObject.get("protein").toString())/weightfood;
                        float carbohydrate = (float) Double.parseDouble(itemJsonObject.get("carbohydrate").toString())/weightfood;
                        float fat = (float) Double.parseDouble(itemJsonObject.get("fat").toString())/weightfood;
                        float salt = (float) Double.parseDouble(itemJsonObject.get("salt").toString())/weightfood;
                        float calorie = (float) Double.parseDouble(itemJsonObject.get("calorie").toString())/weightfood;
                        weightfood /= weightfood;

                        Nutrient base = new Nutrient(nameList[i], carbohydrate, protein, fat, salt, calorie, weightfood);
                        Log.d("base", base.toString());
                        wp.add(base);
                        i++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                }
                ArrayList<Nutrient> chagnedNutrient = applyWeight(wp, eatenList);
                Log.d("applyWeight", chagnedNutrient.toString());
                //calorie랑 sum 설정, 이름 disable, 칼로리 enable
                float carb=0f, prot=0f, fat=0f, salt =0f;
                for(int i=0; i<chagnedNutrient.size(); i++) {
                    EditText foodCalorie = listFoodView.getChildAt(i).findViewById(R.id.updateFoodCalorieVeiw);
                    EditText foodName = listFoodView.getChildAt(i).findViewById(R.id.updateFoodNameView);
                    foodCalorie.setHint(String.valueOf(chagnedNutrient.get(i).getCalorie()));
                    foodCalorie.setText(String.valueOf(chagnedNutrient.get(i).getCalorie()));
                    carb += chagnedNutrient.get(i).getCarbohydrate();
                    prot += chagnedNutrient.get(i).getProtein();
                    fat += chagnedNutrient.get(i).getFat();
                    salt += chagnedNutrient.get(i).getSalt();
                    disableEditText(foodName);
                    enableEditText(foodCalorie);
                }
                carbohydrateEditText.setText(String.valueOf(carb));
                carbohydrateEditText.setHint(String.valueOf(carb));
                disableEditText(carbohydrateEditText);
                proteinEditText.setText(String.valueOf(prot));
                proteinEditText.setHint(String.valueOf(prot));
                disableEditText(proteinEditText);
                fatEditText.setText(String.valueOf(fat));
                fatEditText.setHint(String.valueOf(fat));
                disableEditText(fatEditText);
                saltEditText.setText(String.valueOf(salt));
                saltEditText.setHint(String.valueOf(salt));
                disableEditText(saltEditText);

                secondButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.GONE);
                String result = "Finish 1st step. Start 2nd step." + "\n" + "Change the calorie of food if you want. " + "\n" + "Push 'change calorie' button after setting the calories.";
                noticeView.setText(result);

            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(UpdateDietActivity.this, "error happened", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());
            }
        });
    }

    public ArrayList<Nutrient> applyWeight(ArrayList<Nutrient> nt, int[] eatenWeight) {
        ArrayList<Nutrient> new_nt = new ArrayList<>();
        for(int i =0; i<nt.size(); i++) {
            int ew =eatenWeight[i];
            Log.d("ew", String.valueOf(ew));
            float carb = nt.get(i).getCarbohydrate()*ew;
            Log.d("carb", String.valueOf(carb));
            float prot = nt.get(i).getProtein()*ew;
            float fat = nt.get(i).getFat()*ew;
            float salt = nt.get(i).getSalt()*ew;
            float weight = nt.get(i).getWeightfood()*ew;
            float calorie = nt.get(i).getCalorie()*ew;
            String name =  nt.get(i).getNamefood();
            Nutrient chagned = new Nutrient(name, carb, prot, fat, salt, calorie, weight);
            new_nt.add(chagned);
        }
        return new_nt;
    }
}
