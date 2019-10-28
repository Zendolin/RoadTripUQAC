package com.uqac.mobile.roadtripplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class BottomBarFragment extends Fragment {

    private ImageView imageFriends;
    private TextView textFriends;

    private ImageView imagePlanATrip;
    private TextView textPlanATrip;

    private ImageView imageProfile;
    private TextView textProfile;


    private ImageView imageMyTrips;
    private TextView textMyTrips;

    private ImageView imageChat;
    private TextView textChat;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottombar_fragment_layout, container, false);


        imageFriends = view.findViewById(R.id.bb_imageViewFriends);
        textFriends = view.findViewById(R.id.bb_textViewFriends);

        imagePlanATrip = view.findViewById(R.id.bb_imageViewPlanATrip);
        textPlanATrip = view.findViewById(R.id.bb_textViewPlanATrip);

        imageProfile = view.findViewById(R.id.bb_imageViewProfile);
        textProfile = view.findViewById(R.id.bb_textViewProfile);

        imageMyTrips = view.findViewById(R.id.bb_imageViewMyTrips);
        textMyTrips = view.findViewById(R.id.bb_textViewMyTrips);

        imageChat = view.findViewById(R.id.bb_imageViewChat);
        textChat = view.findViewById(R.id.bb_textViewChat);

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

        imagePlanATrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPlanATrip();
            }
        });
        textPlanATrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPlanATrip();
            }
        });

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

        imageMyTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMyTrips();
            }
        });
        textMyTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMyTrips();
            }
        });

        imageChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickChat();
            }
        });
        textChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickChat();
            }
        });

        return view;
    }

    private void clickProfile()
    {
        ((MainActivity)getActivity()).changeFragment("ProfileFragment");
    }

    private void clickMyTrips()
    {
        ((MainActivity)getActivity()).changeFragment("MyTripsFragments");
    }

    private void clickPlanATrip()
    {
        ((MainActivity)getActivity()).changeFragment("MapFragment");
    }
    private void clickChat()
    {
        ((MainActivity)getActivity()).changeFragment("ChatFragment");
    }

    private void clickFriends()
    {
        ((MainActivity)getActivity()).changeFragment("SettingsFragments");
    }
}
