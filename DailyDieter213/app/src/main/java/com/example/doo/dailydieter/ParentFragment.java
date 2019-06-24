package com.example.doo.dailydieter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ParentFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.parent_frame_layout, CommunityFragment.newInstance());
        transaction.commit();

    }


    public static ParentFragment newInstance() {
        ParentFragment fragment = new ParentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent, null);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)view.findViewById(R.id.navigation_parent);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_community:
                                selectedFragment = CommunityFragment.newInstance();
                                break;
                            case R.id.navigation_tip:
                                selectedFragment = TipFragment.newInstance();
                                break;
                            case R.id.navigation_celebrity:
                                selectedFragment = CelebrityFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        transaction.replace(R.id.parent_frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });


        return view;

    }
}
