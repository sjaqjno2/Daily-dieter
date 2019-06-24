package com.example.doo.dailydieter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

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

public class MyPageFragment extends Fragment {

    private String id;
    private String name;
    private int height;
    private int weight;
    private String phone;
    private String email;
    private String gender;
    public ImageButton wishlist;
    public MyPageFragment() {

    }

    public static MyPageFragment newInstance() {
        MyPageFragment fragment = new MyPageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, null);
        wishlist = view.findViewById(R.id.wishlist);
        wishlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity() , WishListActivity.class);
                startActivity(intent);
            }
        });


        ImageButton logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                SharedPreferences pref = getActivity().getSharedPreferences("token", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                Intent go_first = new Intent(getActivity(), LoginActivity.class);
                go_first.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                go_first.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(go_first);
                onDestroy();

            }
        });
        ImageButton myInfo = view.findViewById(R.id.myInfoImageButton);
        myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_info = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(go_info);
            }
        });
        return view;


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



}