package com.uqac.mobile.roadtripplanner.Profiles;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.uqac.mobile.roadtripplanner.MyTrip;
import com.uqac.mobile.roadtripplanner.R;

import androidx.fragment.app.Fragment;

public class MyTripSquareFragment extends Fragment  implements OnMapReadyCallback{
    private static String TAG = "----------RoadTrip Planner-------------";
    private MyTrip m;
    private View view;
    private TextView textTripName;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.mytrip_square_fragment_layout, container, false);
        return view;
    }

    public void initMap(MyTrip m)
    {   View view = this.getView();
        textTripName = view.findViewById(R.id.myTripSquare_TripName);
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapFragment);
        this.m = m;
        if(!m.tripName.isEmpty()) textTripName.setText(m.tripName);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setMinZoomPreference(8.0f);

        Double longitude = m.listStages.get(0).longitude;
        Double latitude = m.listStages.get(0).latitude;

        LatLng coords = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
    }
}
