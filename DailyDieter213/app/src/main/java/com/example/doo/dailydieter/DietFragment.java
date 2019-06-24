package com.example.doo.dailydieter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class DietFragment extends android.support.v4.app.Fragment {

    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private BluetoothSPP bt;
    ListView listDiet;
    ArrayList<ListDiet> listDiets;
    ArrayList<String> al;

    public DietFragment() {

    }
    public static DietFragment newInstance() {
        DietFragment fragment = new DietFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bt = new BluetoothSPP(getActivity()); //Initializing

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            onStop();
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        }

    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet, null);
        TextView textView = view.findViewById(R.id.date);
        CompactCalendarView compactCalendarView = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy.MM.dd", Locale.KOREA );
        Date currentTime = new Date();
        sticker();

        String today = simpleDateFormat.format(currentTime);
        textView.setText(today);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(30);

        Log.d("1212",String.valueOf(currentTime.getTime()));
        Event ev1 = new Event(Color.GREEN, 1538030163747L, "Some 1");
        Event ev2 = new Event(Color.BLUE, 1538030163747L, "Some 2");
        compactCalendarView.addEvent(ev1);
        compactCalendarView.addEvent(ev2);
        List<Event> event = compactCalendarView.getEvents(1538030163747L);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                String dateselected = String.valueOf(dateClicked);
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);

                String hi = String.valueOf(dateselected);
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
                getDiet(b);
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });
        return view;
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

                    if (String.valueOf(date.trim()).equals(String.valueOf(yymmdd.trim()))) {
                        listDiets.add(diet);
                        if(mealtime.contains("breakfast")) {
                            Event ev1 = new Event(Color.GREEN, 1538030163747L, "Some 1");
                        }
                        Event ev2 = new Event(Color.BLUE, 1538030163747L, "Some 2");
                    }

                    if (listDiets.size() != 0) {
                        Log.d("listDiets: ", String.valueOf(listDiets));
                        ListView list = (ListView) getActivity().findViewById(R.id.listDiet);
                        ListDietAdapter adapter = new ListDietAdapter(getActivity(), listDiets);
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

                                Intent intent = new Intent(getActivity(), SelectedDietActivity2.class);
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
                if(mList.size() == 0){
                    listDiets.clear();
                    ListView list = (ListView) getActivity().findViewById(R.id.listDiet);
                    ListDietAdapter adapter = new ListDietAdapter(getActivity(), listDiets);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(getActivity(), "error happened when getting diet", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());
            }
        });
    }

    private String loadToken() {
        SharedPreferences pref = getActivity().getSharedPreferences("Token", Activity.MODE_PRIVATE);
        String token = pref.getString("token", null);
        return token;
    }

    public void sticker(){
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

        Call<JsonArray> a = service.sticker();
        a.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                CompactCalendarView compactCalendarView = getView().findViewById(R.id.compactcalendar_view);
                al = new ArrayList<>();
                al.clear();

                JsonArray mList = response.body();
                Log.d("mList", mList.toString());
                for (JsonElement item : mList) {

                    JsonObject itemJson = item.getAsJsonObject();

                    String date = itemJson.get("date").getAsString();
                    String mealtime = itemJson.get("mealtime").getAsString();

                    if (mList.size() != 0) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            Long changedDate = simpleDateFormat.parse(date).getTime();

                            if(mealtime.equals("breakfast")) {
                                Event ev1 = new Event(Color.GREEN, changedDate);
                                compactCalendarView.addEvent(ev1);

                            } else if(mealtime.equals("lunch")){
                                Event ev2 = new Event(Color.BLUE, changedDate);
                                compactCalendarView.addEvent(ev2);

                            } else if(mealtime.equals("dinner")){
                                Event ev3 = new Event(Color.YELLOW, changedDate);
                                compactCalendarView.addEvent(ev3);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(getActivity(), "Sticker error", Toast.LENGTH_SHORT).show();
                Log.d("err", t.toString());
            }
        });
    }
    /*



     */
/*public static Map<Long, List<Event>> getMultipleEventsForEachDayAsMap(int start, int days, long timeStamp) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        Map<Long, List<Event>> epochMillisToEvents = new HashMap<>();
        for (int i = start; i < days; i++) {
            setDateTime(timeStamp, currentCalender, i);
            List<Event> eventList = new ArrayList<>();
            List<Event> events = Arrays.asList(new Event(Color.BLUE, currentCalender.getTimeInMillis()), new Event(Color.RED, currentCalender.getTimeInMillis() + 3600 * 1000), new Event(Color.RED, currentCalender.getTimeInMillis() + (3600 * 2) * 1000), new Event(Color.RED, currentCalender.getTimeInMillis() + (3600 * 3) * 1000));
            eventList.addAll(events);
            epochMillisToEvents.put(currentCalender.getTimeInMillis(), eventList);
        }
        return epochMillisToEvents;
    }*//*


    private void drawSingleEvent(Canvas canvas, float xPosition, float yPosition, List<Event> eventsList) {
        Event event = eventsList.get(0);
        drawEventIndicatorCircle(canvas, xPosition, yPosition, event.getColor());
    }

    private void drawTwoEvents(Canvas canvas, float xPosition, float yPosition, List<Event> eventsList) {
        //draw fist event just left of center
        drawEventIndicatorCircle(canvas, xPosition + (xIndicatorOffset * -1), yPosition, eventsList.get(0).getColor());
        //draw second event just right of center
        drawEventIndicatorCircle(canvas, xPosition + (xIndicatorOffset * 1), yPosition, eventsList.get(1).getColor());
    }
    private void drawEventsWithPlus(Canvas canvas, float xPosition, float yPosition, List<Event> eventsList) {
        // display more than 2 evens before displaying plus indicator, but don't draw more than 3 indicators for now
        for (int j = 0, k = -2; j < 3; j++, k += 2) {
            Event event = eventsList.get(j);
            float xStartPosition = xPosition + (xIndicatorOffset * k);
            if (j == 2) {
                dayPaint.setColor(multiEventIndicatorColor);
                dayPaint.setStrokeWidth(multiDayIndicatorStrokeWidth);
                canvas.drawLine(xStartPosition - smallIndicatorRadius, yPosition, xStartPosition + smallIndicatorRadius, yPosition, dayPaint);
                canvas.drawLine(xStartPosition, yPosition - smallIndicatorRadius, xStartPosition, yPosition + smallIndicatorRadius, dayPaint);
                dayPaint.setStrokeWidth(0);
            } else {
                drawEventIndicatorCircle(canvas, xStartPosition, yPosition, event.getColor());
            }
        }
    }


    void drawEvents(Canvas canvas, Calendar currentMonthToDrawCalender, int offset) {
        int currentMonth = currentMonthToDrawCalender.get(Calendar.MONTH);
        List<CalendarContract.Events> uniqEvents = eventsContainer.getEventsForMonthAndYear(currentMonth, currentMonthToDrawCalender.get(Calendar.YEAR));
        boolean shouldDrawCurrentDayCircle = currentMonth == todayCalender.get(Calendar.MONTH);
        boolean shouldDrawSelectedDayCircle = currentMonth == currentCalender.get(Calendar.MONTH);
        int todayDayOfMonth = todayCalender.get(Calendar.DAY_OF_MONTH);
        int currentYear = todayCalender.get(Calendar.YEAR);
        int selectedDayOfMonth = currentCalender.get(Calendar.DAY_OF_MONTH);
        float indicatorOffset = bigCircleIndicatorRadius / 2;
        if (uniqEvents != null) {
            for (int i = 0; i < uniqEvents.size(); i++) {
                CalendarContract.Events events = uniqEvents.get(i);
                long timeMillis = events.getTimeInMillis();
                eventsCalendar.setTimeInMillis(timeMillis);
                int dayOfWeek = getDayOfWeek(eventsCalendar);
                int weekNumberForMonth = eventsCalendar.get(Calendar.WEEK_OF_MONTH);
                float xPosition = widthPerDay * dayOfWeek + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight;
                float yPosition = weekNumberForMonth * heightPerDay + paddingHeight;
                if (((animationStatus == EXPOSE_CALENDAR_ANIMATION || animationStatus == ANIMATE_INDICATORS) && xPosition >= growFactor) || yPosition >= growFactor) {
                    // only draw small event indicators if enough of the calendar is exposed
                    continue;
                } else if (animationStatus == EXPAND_COLLAPSE_CALENDAR && yPosition >= growFactor) {
                    // expanding animation, just draw event indicators if enough of the calendar is visible
                    continue;
                } else if (animationStatus == EXPOSE_CALENDAR_ANIMATION && (eventIndicatorStyle == FILL_LARGE_INDICATOR || eventIndicatorStyle == NO_FILL_LARGE_INDICATOR)) {
                    // Don't draw large indicators during expose animation, until animation is done
                    continue;
                }
                List<Event> eventsList = events.getEvents();
                int dayOfMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                int eventYear = eventsCalendar.get(Calendar.YEAR);
                boolean isSameDayAsCurrentDay = shouldDrawCurrentDayCircle && (todayDayOfMonth == dayOfMonth) && (eventYear == currentYear);
                boolean isCurrentSelectedDay = shouldDrawSelectedDayCircle && (selectedDayOfMonth == dayOfMonth);
                if (shouldDrawIndicatorsBelowSelectedDays || (!shouldDrawIndicatorsBelowSelectedDays && !isSameDayAsCurrentDay && !isCurrentSelectedDay) || animationStatus == EXPOSE_CALENDAR_ANIMATION) {
                    if (eventIndicatorStyle == FILL_LARGE_INDICATOR || eventIndicatorStyle == NO_FILL_LARGE_INDICATOR) {
                        if (!eventsList.isEmpty()) {
                            Event event = eventsList.get(0);
                            drawEventIndicatorCircle(canvas, xPosition, yPosition, event.getColor());
                        }
                    } else {
                        yPosition += indicatorOffset;
                        // this makes sure that they do no overlap
                        if (shouldDrawIndicatorsBelowSelectedDays && (isSameDayAsCurrentDay || isCurrentSelectedDay)) {
                            yPosition += indicatorOffset;
                        }
                        if (eventsList.size() >= 3) {
                            drawEventsWithPlus(canvas, xPosition, yPosition, eventsList);
                        } else if (eventsList.size() == 2) {
                            drawTwoEvents(canvas, xPosition, yPosition, eventsList);
                        } else if (eventsList.size() == 1) {
                            drawSingleEvent(canvas, xPosition, yPosition, eventsList);
                        }
                    }
                }
            }
        }
    }

    private void drawEventIndicatorCircle(Canvas canvas, float x, float y, int color) {
        dayPaint.setColor(color);
        if (eventIndicatorStyle == SMALL_INDICATOR) {
            dayPaint.setStyle(Paint.Style.FILL);
            drawCircle(canvas, smallIndicatorRadius, x, y);
        } else if (eventIndicatorStyle == NO_FILL_LARGE_INDICATOR){
            dayPaint.setStyle(Paint.Style.STROKE);
            drawDayCircleIndicator(NO_FILL_LARGE_INDICATOR, canvas, x, y, color);
        } else if (eventIndicatorStyle == FILL_LARGE_INDICATOR) {
            drawDayCircleIndicator(FILL_LARGE_INDICATOR, canvas, x, y, color);
        }
    }

    private void drawDayCircleIndicator(int indicatorStyle, Canvas canvas, float x, float y, int color) {
        drawDayCircleIndicator(indicatorStyle, canvas, x, y, color, 1);
    }

    private void drawDayCircleIndicator(int indicatorStyle, Canvas canvas, float x, float y, int color, float circleScale) {
        float strokeWidth = dayPaint.getStrokeWidth();
        if (indicatorStyle == NO_FILL_LARGE_INDICATOR) {
            dayPaint.setStrokeWidth(2 * screenDensity);
            dayPaint.setStyle(Paint.Style.STROKE);
        } else {
            dayPaint.setStyle(Paint.Style.FILL);
        }
        drawCircle(canvas, x, y, color, circleScale);
        dayPaint.setStrokeWidth(strokeWidth);
        dayPaint.setStyle(Paint.Style.FILL);
    }

    private void drawCircle(Canvas canvas, float x, float y, int color, float circleScale) {
        dayPaint.setColor(color);
        if (animationStatus == ANIMATE_INDICATORS) {
            float maxRadius = circleScale * bigCircleIndicatorRadius * 1.4f;
            drawCircle(canvas, growfactorIndicator > maxRadius ? maxRadius: growfactorIndicator, x, y - (textHeight / 6));
        } else {
            drawCircle(canvas, circleScale * bigCircleIndicatorRadius, x, y - (textHeight / 6));
        }
    }

    private void drawCircle(Canvas canvas, float radius, float x, float y) {
        canvas.drawCircle(x, y, radius, dayPaint);
    }
*/





}