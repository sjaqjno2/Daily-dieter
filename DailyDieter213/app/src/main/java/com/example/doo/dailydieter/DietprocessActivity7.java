package com.example.doo.dailydieter;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

    public class DietprocessActivity7 extends AppCompatActivity {

        public float protein1;
        public float carbohydrate1;
        public float fat1;
        public float salt1;
        public float calorie1;
        public float weightfood1;

        public String mealtime;



    public ArrayList<Nutrient> arraylist = new ArrayList<Nutrient>();

    public static int food_average_1, food_average_2, food_average_3, food_average_4, food_average_5;
    private BluetoothSPP bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity7_dietprocess);
        SharedPreferences spref = getSharedPreferences("Cropped Image", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.clear();

        Intent in3 = getIntent();

        String namefood1 = in3.getStringExtra("choice_food1");
        String namefood2 = in3.getStringExtra("choice_food2");
        String namefood3 = in3.getStringExtra("choice_food3");
        String namefood4 = in3.getStringExtra("choice_food4");
        String namefood5 = in3.getStringExtra("choice_food5");

//        String[] namelist = new String[]{"tomato", "banana", "cabbage kimchi", "rice", "sullungtang"};
        String[] namelist = new String[]{namefood1, namefood2, namefood3, namefood4, namefood5};
        for (String name : namelist) {
            getData(name);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        Calendar myCalendar = Calendar.getInstance();
//
//        DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                // TODO Auto-generated method stub
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//            }
//
//        };

        Button registerFood = (Button)findViewById(R.id.registerFoodButton);
//        registerFood.setEnabled(false);

        registerFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> array_namefood = new ArrayList<>();
                ArrayList<Float> array_float = new ArrayList<>();
                float sum_carb = 0f;
                float sum_prot = 0f;
                float sum_fat = 0f;
                float sum_salt = 0f;

                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.mealradio);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId != -1) {  //현재 채크된 버튼이 없을때 -1값을 전달

                            RadioButton breakfast = (RadioButton)findViewById(R.id.breakfast);
                            RadioButton lunch = (RadioButton)findViewById(R.id.lunch);
                            RadioButton dinner = (RadioButton)findViewById(R.id.dinner);

                            if (breakfast != null) {
                                mealtime = "breakfast";
                            } if(lunch != null) {
                                mealtime = "lunch";
                            } if(dinner != null) {
                                mealtime = "dinner";
                            }
                        }
                    }
                });

//                DatePickerDialog dialog = new DatePickerDialog(DietprocessActivity7.this, datepicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH));
//                dialog.setTitle("Pick a date");
//                dialog.show();
//
//                String myFormat = "yyyy/MM/dd"; //In which you need put here
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
//
//                String date = sdf.format(myCalendar.getTime());

                int size = arraylist.size();
                for(int i=0; i< size; i++) {
                    array_namefood.add(arraylist.get(i).getNamefood());
                    array_float.add(arraylist.get(i).getCalorie());
                    sum_carb += arraylist.get(i).getCarbohydrate();
                    sum_prot += arraylist.get(i).getProtein();
                    sum_fat += arraylist.get(i).getFat();
                    sum_salt += arraylist.get(i).getSalt();
                }
                registerDiet(array_namefood.get(0), array_float.get(0),
                        array_namefood.get(1), array_float.get(1),
                        array_namefood.get(2), array_float.get(2),
                        array_namefood.get(3), array_float.get(3),
                        array_namefood.get(4), array_float.get(4),
                        (int)arraylist.get(0).getWeightfood(),(int)arraylist.get(1).getWeightfood(),(int)arraylist.get(2).getWeightfood(),
                        (int)arraylist.get(3).getWeightfood(),(int)arraylist.get(4).getWeightfood(),
                        sum_carb, sum_prot, sum_fat, sum_salt, mealtime);
            }
            //ToDo average 교체
        });

        bt = new BluetoothSPP(this); //Initializing
        FloatingActionButton btnConnect = findViewById(R.id.btnConnect2); //연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        TextView textview1 =  findViewById(R.id.weight_text1);
        TextView textview2 = findViewById(R.id.weight_text2);
        TextView textview3 =  findViewById(R.id.weight_text3);
        TextView textview4 = findViewById(R.id.weight_text4);
        TextView textview5 = findViewById(R.id.weight_text5);
        Button weighing = findViewById(R.id.calculateButton);
        ArrayList<Integer> food_array_1 = new ArrayList<>();
        ArrayList<Integer> food_array_2 = new ArrayList<>();
        ArrayList<Integer> food_array_3 = new ArrayList<>();
        ArrayList<Integer> food_array_4 = new ArrayList<>();
        ArrayList<Integer> food_array_5 = new ArrayList<>();
        ArrayList<Integer> food_array_6 = new ArrayList<>();
        ArrayList<Integer> food_array_7 = new ArrayList<>();
        ArrayList<Integer> food_array_8 = new ArrayList<>();
        ArrayList<Integer> food_array_9 = new ArrayList<>();
        ArrayList<Integer> food_array_10 = new ArrayList<>();

        weighing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
                    public void onDataReceived(byte[] data, String message) {
                        Log.d("Message_orgin", message);
                        for(int i = 0; i<10; i++){
                            String[] words =message.split(" ");
                            Log.d("message", words[0]);
                            Integer casted_a = Integer.parseInt(words[0]);
                            food_array_1.add(casted_a);

                            Integer casted_b = Integer.parseInt(words[1]);
                            food_array_2.add(casted_b);

                            Integer casted_c = Integer.parseInt(words[2]);
                            food_array_3.add(casted_c);

                            Integer casted_d = Integer.parseInt(words[3]);
                            food_array_4.add(casted_d);

                            Integer casted_e = Integer.parseInt(words[4]);
                            food_array_5.add(casted_e);

                            i++;

                        }

                    }
                });
                Log.d("avg_size", String.valueOf(food_array_1.size()));
                food_average_1 = getAverage(food_array_1);
                food_average_2 = getAverage(food_array_2);
                food_average_3 = getAverage(food_array_3);
                food_average_4 = getAverage(food_array_4);
                food_average_5 = getAverage(food_array_5);

                Log.d("avg1", String.valueOf(food_average_1));
                textview1.setText(String.valueOf(food_average_1));
                textview2.setText(String.valueOf(food_average_2));
                textview3.setText(String.valueOf(food_average_3));
                textview4.setText(String.valueOf(food_average_4));
                textview5.setText(String.valueOf(food_average_5));

            }
        });

        Button reweighing = (Button)findViewById(R.id.reweighing);
        reweighing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
                    public void onDataReceived(byte[] data, String message) {
                        for(int i = 0; i<10; i++){
                            String[] words =message.split(" ");
                            Log.d("message", words[0]);
                            Integer casted_a = Integer.parseInt(words[0]);
                            Log.d("message_casted", String.valueOf(casted_a));
                            food_array_6.add(casted_a);

                            Integer casted_b = Integer.parseInt(words[1]);
                            food_array_7.add(casted_b);

                            Integer casted_c = Integer.parseInt(words[2]);
                            food_array_8.add(casted_c);

                            Integer casted_d = Integer.parseInt(words[3]);
                            food_array_9.add(casted_d);

                            Integer casted_e = Integer.parseInt(words[4]);
                            food_array_10.add(casted_e);

                            i++;

                        }

                    }
                });
                arraylist.clear();
                int food_average_6 = getAverage(food_array_6);
                int food_average_7 = getAverage(food_array_7);
                int food_average_8 = getAverage(food_array_8);
                int food_average_9 = getAverage(food_array_9);
                int food_average_10 = getAverage(food_array_10);

                int a = food_average_1 - food_average_6;
                int b = food_average_2 - food_average_7;
                int c = food_average_3 - food_average_8;
                int d = food_average_4 - food_average_9;
                int e = food_average_5 - food_average_10;

                a= Math.abs(a);
                b= Math.abs(b);
                c= Math.abs(c);
                d= Math.abs(d);
                e= Math.abs(e);

                textview1.setText(String.valueOf(a));
                textview2.setText(String.valueOf(b));
                textview3.setText(String.valueOf(c));
                textview4.setText(String.valueOf(d));
                textview5.setText(String.valueOf(e));

                registerFood.setEnabled(true);
                int[] weightList = {a,b,c,d,e};

                for(int i =0 ; i<namelist.length; i++) {
                    calculateWeight(namelist[i], weightList[i]);
                }
            }
        });


            bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
                public void onDeviceConnected(String name, String address) {
//                Toast.makeText(getActivity().getApplicationContext(), "Connected to " + name + "\n" + address    , Toast.LENGTH_SHORT).show();
                    Toast.makeText(DietprocessActivity7.this, "Connected to " + name + "\n" + address, Toast.LENGTH_SHORT).show();

                }

                public void onDeviceDisconnected() { //연결해제
//                Toast.makeText(getActivity().getApplicationContext()                        , "Connection lost", Toast.LENGTH_SHORT).show();
                    Toast.makeText(DietprocessActivity7.this, "Connection lost", Toast.LENGTH_SHORT).show();
                }

                public void onDeviceConnectionFailed() { //연결실패
//                Toast.makeText(getActivity().getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();

                    Toast.makeText(DietprocessActivity7.this, "Unable to connect", Toast.LENGTH_SHORT).show();
                }
            });
        }


    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {

    }

    public int getAverage(ArrayList<Integer> al){
        int total = 0;
        int avg = 0;
        for(int j = 0; j < al.size(); j++)
        {
            avg = al.get(al.size()-1);
        }
        Log.d("total", String.valueOf(total));
        Log.d("avg", String.valueOf(avg));
        return avg;
    }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(DietprocessActivity7.this, "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
                onStop();
            }
        }
    }



    public void getData(String namefood) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonArray> req = service.getNutrientRes(namefood);
        req.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                JsonArray mList = response.body();
                for (JsonElement item : mList) {
                    JsonObject itemJson = item.getAsJsonObject();
                    weightfood1 = itemJson.get("weightfood").getAsFloat();
                    protein1 = itemJson.get("protein").getAsFloat();
                    carbohydrate1 = itemJson.get("carbohydrate").getAsFloat();
                    fat1 = itemJson.get("fat").getAsFloat();
                    salt1 = itemJson.get("salt").getAsFloat();
                    calorie1 = itemJson.get("calorie").getAsFloat();


                    Nutrient wp = new Nutrient(namefood, carbohydrate1, protein1,
                            fat1, salt1, calorie1, weightfood1);
                    if (wp.carbohydrate != 0) {
                        // Binds all strings into an array
                        arraylist.add(wp);
                    }

                    if(arraylist.size() != 0) {
                        ListView list = (ListView) findViewById(R.id.listview);
                        // Pass results to ListViewAdapter Class
                        ListViewAdapter adapter = new ListViewAdapter(DietprocessActivity7.this, arraylist);
                        list.setAdapter(adapter);
                    }
                }
                Log.d("nutrient array",arraylist.toString());

            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(DietprocessActivity7.this, "error happened", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());
            }
        });
    }
    public void registerDiet(String namefood_1, Float calorie_1,
                             String namefood_2, Float calorie_2,
                             String namefood_3, Float calorie_3,
                             String namefood_4, Float calorie_4,
                             String namefood_5, Float calorie_5,
                             int foodweight1, int foodweight2, int foodweight3, int foodweight4, int foodweight5,
                             Float sum_carb, Float sum_prot,
                             Float sum_fat, Float sum_salt, String mealtime) {

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
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://14.63.169.215:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<JsonPrimitive> req = service.registerDiet(namefood_1,calorie_1,namefood_2,calorie_2,namefood_3,calorie_3,namefood_4,calorie_4,namefood_5,calorie_5,
                foodweight1, foodweight2, foodweight3, foodweight4, foodweight5, sum_carb,sum_prot,sum_fat, sum_salt,mealtime);
        req.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                JsonPrimitive mList = response.body();
                String result = mList.toString();
                if(result.equals("\"insert_success\"")){
                    Log.d("들어옴","1)");
                    Intent go_main = new Intent(DietprocessActivity7.this, MainActivity.class);
                    go_main.putExtra("selectedFrag", "diet");
                    startActivity(go_main);
                    finish();
                } else if (result.equals("\"duplication insertion\"")) {
                    Log.d("들어옴", "2");
                    Toast.makeText(DietprocessActivity7.this, "Duplication insertion, Change please", Toast.LENGTH_SHORT).show();
                    
                }else if( result.equals("\"error happened\""));
                    Log.d("err happen", "error" );
                    Toast.makeText(DietprocessActivity7.this, "Insert error happened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Log.d("err", t.toString());
                Toast.makeText(DietprocessActivity7.this, "Insert error happened",Toast.LENGTH_SHORT);
            }
        });
    }
    private String loadToken() {
        SharedPreferences pref = getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }

        public void calculateWeight(String namefood, int eatenlist) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl("http://14.63.169.215:3000/").addConverterFactory(GsonConverterFactory.create()).build();
            RetrofitService service = retrofit.create(RetrofitService.class);
            Call<JsonArray> req = service.getNutrientRes(namefood);
            req.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                    JsonArray mList = response.body();
                    for (JsonElement item : mList) {
                        JsonObject itemJson = item.getAsJsonObject();
                        weightfood1 = itemJson.get("weightfood").getAsFloat();
                        protein1 = (int)itemJson.get("protein").getAsFloat()/weightfood1*eatenlist;
                        carbohydrate1 = (int)itemJson.get("carbohydrate").getAsFloat()/weightfood1*eatenlist;
                        fat1 = (int)itemJson.get("fat").getAsFloat()/weightfood1*eatenlist;
                        salt1 = (int)itemJson.get("salt").getAsFloat()/weightfood1*eatenlist;
                        calorie1 = (int)itemJson.get("calorie").getAsFloat()/weightfood1*eatenlist;
                        weightfood1 = (int)eatenlist;

                        Nutrient wp = new Nutrient(namefood, carbohydrate1, protein1,
                                fat1, salt1, calorie1, weightfood1);
                        if (wp.carbohydrate != 0) {
                            // Binds all strings into an array
                            arraylist.add(wp);
                        }

                        if(arraylist.size() != 0) {
                            ListView list = (ListView) findViewById(R.id.listview);
                            // Pass results to ListViewAdapter Class
                            ListViewAdapter adapter = new ListViewAdapter(DietprocessActivity7.this, arraylist);
                            list.setAdapter(adapter);
                        }
                    }
                    Log.d("nutrient array",arraylist.toString());

                }
                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Toast.makeText(DietprocessActivity7.this, "error happened", Toast.LENGTH_SHORT).show();
                    Log.d("err", t.toString());
                }
            });
        }
}
