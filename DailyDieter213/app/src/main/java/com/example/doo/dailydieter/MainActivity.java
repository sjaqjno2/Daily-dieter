package com.example.doo.dailydieter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backPressCloseHandler = new BackPressCloseHandler(this);

        String frag;
        Intent in3 = getIntent();
        if (in3.hasExtra("selectedFrag")) {
            frag = in3.getStringExtra("selectedFrag");
            if(frag.equals("diet")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, DietFragment.newInstance());
                transaction.commit();
            } else if(frag.equals("community")) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, ParentFragment.newInstance());
                transaction.commit();
            } else if(frag.equals("my page")) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, MyPageFragment.newInstance());
                transaction.commit();
            } else if(frag.equals("group")) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, GroupFragment.newInstance());
                transaction.commit();
            }

        }else {
            //Manually displaying the first fragment - one time only
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, DietFragment.newInstance());
            transaction.commit();
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_camera:
                                selectedFragment = CameraFragment.newInstance();
                                break;
                            case R.id.navigation_diet:
                                selectedFragment = DietFragment.newInstance();
                                break;
                            case R.id.navigation_dashboard:
                                selectedFragment = ParentFragment.newInstance();
                                break;
                            case R.id.navigation_group:
                                selectedFragment = GroupFragment.newInstance();
                                break;
                            case R.id.navigation_mypage:
                                selectedFragment = MyPageFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });
    }
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

}