package com.uqac.mobile.roadtripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.uqac.mobile.roadtripplanner.Adapters.PlacesAdapter;
import com.uqac.mobile.roadtripplanner.Helpers.TaskLoadedCallback;
import com.uqac.mobile.roadtripplanner.Login.LoginActivity;
import com.uqac.mobile.roadtripplanner.Maps.CustomPlace;
import com.uqac.mobile.roadtripplanner.Profiles.Profile;
import com.uqac.mobile.roadtripplanner.Profiles.ProfileFragment;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    static String TAG = "----------RoadTrip Planner-------------";
    public Profile profile;
    public AppBarConfiguration mAppBarConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        String apiKey = getString(R.string.google_maps_key);

        if(!Places.isInitialized()){
            Places.initialize(this, apiKey);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.ContentLayout,new ProfileFragment(),"Profile_FRAGMENT");
        transaction.commit();
        initSideBar();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void initSideBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                //R.id.nav_home,
                //R.id.nav_gallery,
                R.id.nav_map,
                R.id.nav_share,
                R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        /*
        listViewPlaces = findViewById(R.id.places);
        listOfDestinationsAdapter = new PlacesAdapter(this, places);
        listViewPlaces.setAdapter(listOfDestinationsAdapter);*/
        drawer.closeDrawer(GravityCompat.START);
        toolbar.setTitle("RoadTrip Planner");
    }
}
