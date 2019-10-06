package com.uqac.mobile.roadtripplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class BottomBarFragment extends Fragment {

    private ImageView imageProfile;
    private ImageView imageTrips;
    private ImageView imageFriends;
    private ImageView imageSettings;

    private TextView textProfile;
    private TextView textTrips;
    private TextView textFriends;
    private TextView textSettings;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottombar_fragment_layout, container, false);

        imageProfile = view.findViewById(R.id.imageViewProfile);
        imageTrips = view.findViewById(R.id.imageViewTrip);
        imageFriends = view.findViewById(R.id.imageViewFriends);
        imageSettings = view.findViewById(R.id.imageViewSettings);

        textProfile = view.findViewById(R.id.textViewProfile);
        textTrips = view.findViewById(R.id.textViewTrip);
        textFriends = view.findViewById(R.id.textViewFriends);
        textSettings = view.findViewById(R.id.textViewSettings);


        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickProfile();
            }
        });
        textProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickProfile();
            }
        });

        imageTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTrips();
            }
        });
        textTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTrips();
            }
        });

        imageFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFriends();
            }
        });
        textFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFriends();
            }
        });

        imageSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTrips();
            }
        });
        textSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTrips();
            }
        });

        return view;
    }

    private void clickProfile()
    {
        ((MainActivity)getActivity()).changeFragment("ProfileFragment");
    }

    private void clickTrips()
    {
        ((MainActivity)getActivity()).changeFragment("TripsFragments");
    }

    private void clickFriends()
    {
        ((MainActivity)getActivity()).changeFragment("FriendsFragment");
    }

    private void clickSettings()
    {
        ((MainActivity)getActivity()).changeFragment("SettingsFragments");
    }
}
