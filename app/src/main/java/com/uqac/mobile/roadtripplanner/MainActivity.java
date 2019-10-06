package com.uqac.mobile.roadtripplanner;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    static String TAG = "----------RoadTrip Planner-------------";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.ContentLayout,new ProfileFragment(),"Profile_FRAGMENT");
        transaction.commit();

    }

    public void changeFragment(String fragment)
    {
        FragmentManager manager = getSupportFragmentManager();

        ProfileFragment pf = (ProfileFragment) manager.findFragmentByTag("Profile_FRAGMENT");

        if(fragment.equals("ProfileFragment") && pf != null && !pf.isVisible())
        {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.ContentLayout,new ProfileFragment(),"Profile_FRAGMENT");
            transaction.commit();
        }
    }
}
