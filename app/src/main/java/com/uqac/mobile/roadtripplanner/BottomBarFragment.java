package com.uqac.mobile.roadtripplanner;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uqac.mobile.roadtripplanner.Maps.MapFragment;
import com.uqac.mobile.roadtripplanner.Profiles.FriendsListFragment;
import com.uqac.mobile.roadtripplanner.Profiles.MyTripSquareFragment;
import com.uqac.mobile.roadtripplanner.Profiles.ProfileFragment;

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
        imageProfile.setBackgroundColor(Color.GREEN);
        textProfile.setBackgroundColor(Color.GREEN);
        return view;
    }

    private void clickProfile()
    {
        ProfileFragment PF = new ProfileFragment();
        ((MainActivity)getActivity()).changeFragment(PF,"ProfileFragment");
        imageProfile.setBackgroundColor(Color.GREEN);
        imageMyTrips.setBackgroundColor(Color.WHITE);
        imagePlanATrip.setBackgroundColor(Color.WHITE);
        imageChat.setBackgroundColor(Color.WHITE);
        imageFriends.setBackgroundColor(Color.WHITE);

        textProfile.setBackgroundColor(Color.GREEN);
        textMyTrips.setBackgroundColor(Color.WHITE);
        textPlanATrip.setBackgroundColor(Color.WHITE);
        textChat.setBackgroundColor(Color.WHITE);
        textFriends.setBackgroundColor(Color.WHITE);
    }

    private void clickMyTrips()
    {
        MyTripSquareFragment MTF = new MyTripSquareFragment();
        ((MainActivity)getActivity()).changeFragment(MTF,"MyTripsFragments");
        imageProfile.setBackgroundColor(Color.WHITE);
        imageMyTrips.setBackgroundColor(Color.GREEN);
        imagePlanATrip.setBackgroundColor(Color.WHITE);
        imageChat.setBackgroundColor(Color.WHITE);
        imageFriends.setBackgroundColor(Color.WHITE);

        textProfile.setBackgroundColor(Color.WHITE);
        textMyTrips.setBackgroundColor(Color.GREEN);
        textPlanATrip.setBackgroundColor(Color.WHITE);
        textChat.setBackgroundColor(Color.WHITE);
        textFriends.setBackgroundColor(Color.WHITE);
    }

    private void clickPlanATrip()
    {
        MapFragment MF = new MapFragment();
        ((MainActivity)getActivity()).changeFragment(MF,"MapFragment");
        imageProfile.setBackgroundColor(Color.WHITE);
        imageMyTrips.setBackgroundColor(Color.WHITE);
        imagePlanATrip.setBackgroundColor(Color.GREEN);
        imageChat.setBackgroundColor(Color.WHITE);
        imageFriends.setBackgroundColor(Color.WHITE);

        textProfile.setBackgroundColor(Color.WHITE);
        textMyTrips.setBackgroundColor(Color.WHITE);
        textPlanATrip.setBackgroundColor(Color.GREEN);
        textChat.setBackgroundColor(Color.WHITE);
        textFriends.setBackgroundColor(Color.WHITE);
    }
    private void clickChat()
    {
        MapFragment MF = new MapFragment();
        ((MainActivity)getActivity()).changeFragment(MF,"ChatFragment");
        imageProfile.setBackgroundColor(Color.WHITE);
        imageMyTrips.setBackgroundColor(Color.WHITE);
        imagePlanATrip.setBackgroundColor(Color.WHITE);
        imageChat.setBackgroundColor(Color.GREEN);
        imageFriends.setBackgroundColor(Color.WHITE);

        textProfile.setBackgroundColor(Color.WHITE);
        textMyTrips.setBackgroundColor(Color.WHITE);
        textPlanATrip.setBackgroundColor(Color.WHITE);
        textChat.setBackgroundColor(Color.GREEN);
        textFriends.setBackgroundColor(Color.WHITE);
    }

    private void clickFriends()
    {
        FriendsListFragment FF = new FriendsListFragment();
        ((MainActivity)getActivity()).changeFragment(FF,"FriendsFragments");
        imageProfile.setBackgroundColor(Color.WHITE);
        imageMyTrips.setBackgroundColor(Color.WHITE);
        imagePlanATrip.setBackgroundColor(Color.WHITE);
        imageChat.setBackgroundColor(Color.WHITE);
        imageFriends.setBackgroundColor(Color.GREEN);

        textProfile.setBackgroundColor(Color.WHITE);
        textMyTrips.setBackgroundColor(Color.WHITE);
        textPlanATrip.setBackgroundColor(Color.WHITE);
        textChat.setBackgroundColor(Color.WHITE);
        textFriends.setBackgroundColor(Color.GREEN);
    }
}
