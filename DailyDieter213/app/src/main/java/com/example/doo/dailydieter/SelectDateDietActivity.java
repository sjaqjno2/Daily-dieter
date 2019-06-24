package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

public class SelectDateDietActivity extends Activity {
    ListView listDiet;
    ArrayList<ListDiet> listDiets;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date_diet);

        Intent intent = getIntent();
        String dateselected = intent.getStringExtra("date");
        Log.d("date123", dateselected + "112");
        String hi = String.valueOf(dateselected);
        Log.d("hi123",  hi + "112");
        String[] results = hi.split("\\s");

        for (int i = 0; i < results.length; i++) {
            Log.d("results", i + " " + results[i]);
        }
        String month = results[1];
        String date =  results[2];
        String year = results[5];

        if(month.equals("Jan")){
            month = "01";
        } else if(month.equals("Feb")){
            month = "02";
        } else if(month.equals("Mar")){
            month = "03";
        } else if(month.equals("Apr")){
            month = "04";
        } else if(month.equals("May")){
            month = "05";
        } else if(month.equals("Jun")){
            month = "06";
        } else if(month.equals("Jul")){
            month = "07";
        } else if(month.equals("Aug")){
            month = "08";
        } else if(month.equals("Sep")){
            month = "09";
        } else if(month.equals("Oct")){
            month = "10";
        } else if(month.equals("Nov")){
            month = "11";
        } else if(month.equals("Dec")){
            month = "12";
        }
        String a = year + "/" + month + "/" + date;
        String b = String.valueOf(a);
        Log.d("asdfasdf",  a + "gggg");

        getDiet(b);

    }

    public void getDiet(String date){
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

        Call<JsonArray> a = service.getDietCalendar(date);
        a.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listDiets = new ArrayList<>();
                listDiets.clear();

                JsonArray mList = response.body();
                Log.d("mList", mList.toString());
                for(JsonElement item : mList) {

                    JsonObject itemJson = item.getAsJsonObject();
                    int number = itemJson.get("id").getAsInt();
                    String _id = itemJson.get("userid").getAsString();
                    String namefood1 = itemJson.get("namefood1").getAsString();
                    String namefood2 = itemJson.get("namefood2").getAsString();
                    String namefood3 = itemJson.get("namefood3").getAsString();
                    String namefood4 = itemJson.get("namefood4").getAsString();
                    String namefood5 = itemJson.get("namefood5").getAsString();
                    float calorie1 = itemJson.get("calorie1").getAsFloat();
                    float calorie2 = itemJson.get("calorie2").getAsFloat();
                    float calorie3 = itemJson.get("calorie3").getAsFloat();
                    float calorie4 = itemJson.get("calorie4").getAsFloat();
                    float calorie5 = itemJson.get("calorie5").getAsFloat();
                    float sum_carb = itemJson.get("sum_carbohydrate").getAsFloat();
                    float sum_prot = itemJson.get("sum_protein").getAsFloat();
                    float sum_fat = itemJson.get("sum_fat").getAsFloat();
                    float sum_salt = itemJson.get("sum_salt").getAsFloat();
                    int eaten_weight1 = itemJson.get("foodweight1").getAsInt();
                    int eaten_weight2 = itemJson.get("foodweight2").getAsInt();
                    int eaten_weight3 = itemJson.get("foodweight3").getAsInt();
                    int eaten_weight4 = itemJson.get("foodweight4").getAsInt();
                    int eaten_weight5 = itemJson.get("foodweight5").getAsInt();
                    String yymmdd=  itemJson.get("date").getAsString();
                    String mealtime = itemJson.get("mealtime").getAsString();

                    ListDiet diet = new ListDiet(number, _id,
                            namefood1,calorie1, eaten_weight1, namefood2,calorie2, eaten_weight2, namefood3, calorie3, eaten_weight3,
                            namefood4, calorie4, eaten_weight4, namefood5, calorie5, eaten_weight5,
                            sum_carb, sum_prot, sum_fat, sum_salt, yymmdd, mealtime);
                    Log.d("diet", diet.toString());
                    if (diet.userid != null) {
                        // Binds all strings into an array
                        listDiets.add(diet);
                    }
                    if (listDiets.size() != 0) {
                        Log.d("listDiets: ", String.valueOf(listDiets));
                        ListView list = (ListView) findViewById(R.id.listDiet);
                        // Pass results to ListViewAdapter Class
                        ListDietAdapter adapter = new ListDietAdapter(SelectDateDietActivity.this, listDiets);

                        // Binds the Adapter to the ListView
                        list.setAdapter(adapter);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView parent, View view, int position, long id) {
                                int number = listDiets.get(position).getId();
                                String a = listDiets.get(position).getUserId();
                                String b = listDiets.get(position).getMealtime();
                                String c = listDiets.get(position).getDate();
                                int d = listDiets.get(position).getEatenWeight1();
                                int e = listDiets.get(position).getEatenWeight2();
                                int f = listDiets.get(position).getEatenWeight3();
                                int g = listDiets.get(position).getEatenWeight4();
                                int h = listDiets.get(position).getEatenWeight5();
                                Float i = listDiets.get(position).getSum_carb();
                                Float j = listDiets.get(position).getSum_prot();
                                Float k = listDiets.get(position).getSum_fat();
                                Float l = listDiets.get(position).getSum_salt();
                                String namefood1 = listDiets.get(position).getNamefood1();
                                String namefood2 = listDiets.get(position).getNamefood2();
                                String namefood3 = listDiets.get(position).getNamefood3();
                                String namefood4 = listDiets.get(position).getNamefood4();
                                String namefood5 = listDiets.get(position).getNamefood5();
                                Float calorie1 = listDiets.get(position).getCalorie1();
                                Float calorie2 = listDiets.get(position).getCalorie2();
                                Float calorie3 = listDiets.get(position).getCalorie3();
                                Float calorie4 = listDiets.get(position).getCalorie4();
                                Float calorie5 = listDiets.get(position).getCalorie5();



                                Intent intent = new Intent(SelectDateDietActivity.this, SelectedDietActivity.class);
                                intent.putExtra("id", number);
                                intent.putExtra("userid",a);
                                intent.putExtra("mealtime",b);
                                intent.putExtra("date", c);
                                intent.putExtra("eaten_weight1", d);
                                intent.putExtra("eaten_weight2", e);
                                intent.putExtra("eaten_weight3", f);
                                intent.putExtra("eaten_weight4", g);
                                intent.putExtra("eaten_weight5", h);
                                intent.putExtra("sum_carb", i);
                                intent.putExtra("sum_prot", j);
                                intent.putExtra("sum_fat", k);
                                intent.putExtra("sum_salt", l);
                                intent.putExtra("namefood1", namefood1);
                                intent.putExtra("namefood2", namefood2);
                                intent.putExtra("namefood3", namefood3);
                                intent.putExtra("namefood4", namefood4);
                                intent.putExtra("namefood5", namefood5);
                                intent.putExtra("calorie1", calorie1);
                                intent.putExtra("calorie2", calorie2);
                                intent.putExtra("calorie3", calorie3);
                                intent.putExtra("calorie4", calorie4);
                                intent.putExtra("calorie5", calorie5);
                                startActivity(intent);

                            }
                        });

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(SelectDateDietActivity.this, "error happened when getting diet", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());
            }
        });
    }
    private String loadToken() {
        SharedPreferences pref = this.getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }

    /*public void selected() {

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                String a = user_list.get(position).getTitle();
                String b = user_list.get(position).getContent();
                String writerid = user_list.get(position).getNickname();

                Intent intent = new Intent(SelectDateDietActivity.this, SelectedDietActivity.class);
                intent.putExtra("title",a);
                intent.putExtra("content",b);
                intent.putExtra("writerid", writerid);

                startActivity(intent);

            }
        });
    }*/

}
