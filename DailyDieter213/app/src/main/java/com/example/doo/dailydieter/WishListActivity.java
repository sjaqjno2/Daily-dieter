package com.example.doo.dailydieter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class WishListActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        //layout
        setContentView(R.layout.activity_wish_list);

        // frame_layout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_wish, WishListPostFragment.newInstance());
        transaction.commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.wish_list_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_wish_post:
                                selectedFragment = WishListPostFragment.newInstance();
                                break;
                            case R.id.navigation_wish_tip:
                                selectedFragment = WishListTipFragment.newInstance();
                                break;
                            case R.id.navigation_wish_celebrity:
                                selectedFragment = WishListCelebrityFragment.newInstance();
                                break;
                        }
                        //layout
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout_wish, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });
    }
}