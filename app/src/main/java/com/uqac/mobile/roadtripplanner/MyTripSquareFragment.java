package com.uqac.mobile.roadtripplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class MyTripSquareFragment extends Fragment {
    private static String TAG = "----------RoadTrip Planner-------------";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mytrip_square_fragment_layout, container, false);

        return view;
    }
}
