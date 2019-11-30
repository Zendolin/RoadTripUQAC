package com.uqac.mobile.roadtripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.uqac.mobile.roadtripplanner.Login.LoginActivity;
import com.uqac.mobile.roadtripplanner.Profiles.Profile;
import com.uqac.mobile.roadtripplanner.Profiles.ProfileFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    static String TAG = "----------RoadTrip Planner-------------";
    public Profile profile;
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

    public void changeFragment(Fragment fragment, String tag){
        Log.d(TAG,"------changing fragment to : " + fragment + "-------");
        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.ContentLayout, fragment,tag);
        transaction.commit();
    }

    public void exit()
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
