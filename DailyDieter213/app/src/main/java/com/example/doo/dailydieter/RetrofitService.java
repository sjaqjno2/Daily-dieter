package com.example.doo.dailydieter;

import android.provider.ContactsContract;
import android.util.JsonToken;

import com.google.api.client.util.Data;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface RetrofitService {

    @GET("/nutrient")
    Call<JsonArray> getNutrientRes(@Query("namefood") String namefood);

    @GET("/nutrientArray")
    Call<JsonArray> getNutrientResFromArray(@Query("namefood1") String namefood1, @Query("namefood2") String namefood2, @Query("namefood3") String namefood3,
                                        @Query("namefood4") String namefood4, @Query("namefood5") String namefood5);

    @GET("/registerID")
    Call<JsonObject> getRegisterRES(@Query("id") String id, @Query("password") String password, @Query("name") String name,
                                    @Query("height") int height, @Query("weight") int weight, @Query("phone") String phone,
                                    @Query("email") String email, @Query("gender") String gender);

    @POST("/community")
    Call<JsonObject> getWritingRES(@Query("title") String title, @Query("content") String content);

    @POST("/getPost")
    Call<JsonArray> user(@Query("title") String title, @Query("content") String content);

    @POST("/updatePost")
    Call<JsonObject> updatePost(@Query("new_title") String new_title, @Query("new_content") String new_content, @Query("original_title") String original_title, @Query("original_content") String original_content);

    @POST("/deletePost")
    Call<JsonArray> deletePost(@Query("title") String title, @Query("content") String content);

    @POST("/logintest")
    Call<JsonObject> login(@Query("id") String id, @Query("password") String password);

    @POST("/image")
    Call<Data> sendImage(@Part("id") RequestBody id, @Part MultipartBody.Part img);

    @GET("/getDietCalendar")
    Call<JsonArray> getDietCalendar(@Query("dateclicked") String date);

    @GET("/getUserInfo")
    Call<JsonArray> getUserInfo();

    @POST("/confirmWriter")
    Call<JsonPrimitive> confirmWriter(@Query("title") String title, @Query("content") String content);

    @GET("/registerDiet")
    Call<JsonPrimitive> registerDiet(@Query("namefood1") String namefood1, @Query("calorie1") Float calorie1,
                                 @Query("namefood2") String namefood2, @Query("calorie2") Float calorie2,
                                 @Query("namefood3") String namefood3, @Query("calorie3") Float calorie3,
                                 @Query("namefood4") String namefood4, @Query("calorie4") Float calorie4,
                                 @Query("namefood5") String namefood5, @Query("calorie5") Float calorie5,
                                 @Query("foodweight1") int foodweight1,
                                 @Query("foodweight2") int foodweight2, @Query("foodweight3") int foodweight3,
                                 @Query("foodweight4") int foodweight4, @Query("foodweight5") int foodweight5,
                                 @Query("sum_carb") Float sum_carb1, @Query("sum_prot") Float sum_prot,
                                 @Query("sum_fat") Float sum_fat, @Query("sum_salt") Float sum_salt, @Query("mealtime") String mealtime);
//                                     @Query("date") String date);

    @POST("/countUp")
    Call<JsonArray> count(@Query("title") String title, @Query("content") String content);

    @GET("/getDiet")
    Call<JsonArray> getDiet();

    @POST("/deleteDiet")
    Call<JsonArray> deleteDiet(@Query("id") int id, @Query("id") String userid, @Query("mealtime") String mealtime, @Query("date") String date);

    @POST("/getDietFromSelection")
    Call<JsonArray> getDietFromSelection(@Query("id") int id);

    @POST("/getTipPost")
    Call<JsonArray> getTipPost();

    @POST("/getCelebrityPost")
    Call<JsonArray> getCelebrityPost();

    @POST("/getGroup")
    Call<JsonArray> getGroup();

    @GET("/makeGroup")
    Call<JsonObject> makeGroup(@Query("groupname") String groupName, @Query("groupgoal") String groupGoal);

    @GET("/changeGoal")
    Call<JsonObject> changeGoal(@Query("groupname") String groupName, @Query("groupgoal") String groupGoal);

    @GET("/participateGroup")
    Call<JsonPrimitive> participateGroup(@Query("groupname") String groupName);

    @GET("/checkParticipation")
    Call<JsonPrimitive> checkParticipation(@Query("groupname") String groupName);

    @GET("/checkProducer")
    Call<JsonPrimitive> checkProducer(@Query("groupname") String groupName);

    @GET("/userCountUp")
    Call<JsonObject> userCountUp(@Query("groupname") String groupName);

    @POST("/getTipFromSelection")
    Call<JsonArray> getTipFromSelection(@Query("title") String title);

    @POST("/getCelebrityFromSelection")
    Call<JsonArray> getCelebrityFromSelection(@Query("title") String title);

    @POST("/tipCountUp")
    Call<JsonArray> tipCount(@Query("title") String title);

    @POST("/celebrityCountUp")
    Call<JsonArray> celebrityCount(@Query("title") String title);

    @POST("/searchPost")
    Call<JsonArray> searchPost(@Query("search") String search);

    @POST("/searchTip")
    Call<JsonArray> searchTip(@Query("search") String search);

    @POST("/searchCelebrity")
    Call<JsonArray> searchCelebrity(@Query("search") String search);

    @POST("/saveWishlist")
    Call<JsonObject> saveWishlist(@Query("writerid") String writerid, @Query("title") String title, @Query("dashboard") String dashboard);

    @POST("/deleteWish")
    Call<JsonObject> deleteWish(@Query("title") String title, @Query("writerid") String writerid);

    @GET("/getWishPost")
    Call<JsonArray> getWishPost(@Query("dashboard") String dashboard);

    @GET("/getWishTip")
    Call<JsonArray> getWishTip(@Query("dashboard") String dashboard);

    @GET("/getWishCelebrity")
    Call<JsonArray> getWishCelebrity(@Query("dashboard") String dashboard);

    @POST("/updateDiet")
    Call<JsonPrimitive> updateDiet(@Query("id") int id, @Query("namefood1") String namefood1, @Query("calorie1") Float calorie1, @Query("namefood2") String namefood2, @Query("calorie2") Float calorie2,
                               @Query("namefood3") String namefood3, @Query("calorie3") Float calorie3, @Query("namefood4") String namefood4, @Query("calorie4") Float calorie4,
                               @Query("namefood5") String namefood5, @Query("calorie5") Float calorie5,
                               @Query("sum_carb") Float sum_carb, @Query("sum_prot") Float sum_prot, @Query("sum_fat") Float sum_fat, @Query("sum_salt") Float sum_salt);

    @GET("/getDietFromId")
    Call<JsonArray> getDietFromId(@Query("id") int id);

    @POST("/saveMessage")
    Call<JsonPrimitive> saveMessage(@Query("message") String message, @Query("date") String date, @Query("time") String time, @Query("groupID") String groupID);

    @POST("/getMessage")
    Call<JsonArray> getMessage( @Query("groupID") String groupID);

    @GET("/sticker")
    Call<JsonArray> sticker();
}