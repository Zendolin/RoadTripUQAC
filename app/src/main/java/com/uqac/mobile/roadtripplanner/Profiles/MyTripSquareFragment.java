package com.uqac.mobile.roadtripplanner.Profiles;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.uqac.mobile.roadtripplanner.MainActivity;
import com.uqac.mobile.roadtripplanner.MyTrip;
import com.uqac.mobile.roadtripplanner.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class MyTripSquareFragment extends Fragment  implements OnMapReadyCallback{
    private static String TAG = "----------RoadTrip Planner-------------";
    private View view;
    private TextView textTripName;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Profile userProfile;
    private ImageView likeImage;
    private ImageView lockImage;
    private TextView likeText;
    private Profile owner;
    private int index;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.mytrip_square_fragment_layout, container, false);
        likeImage = view.findViewById(R.id.myTripSquare_LikeImage);
        lockImage = view.findViewById(R.id.myTripSquare_LockImage);
        likeText = view.findViewById(R.id.myTripSquare_LikeScore);
        return view;
    }

    public void initMap(int index ,Profile owner)
    {
        this.index = index;
        MyTrip m = owner.trips.get(index);
        userProfile = ((MainActivity)getActivity()).profile;
        View view = this.getView();
        textTripName = view.findViewById(R.id.myTripSquare_TripName);
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapFragment);
        this.owner = owner;
        if(!m.tripName.isEmpty()) textTripName.setText(m.tripName);
        mapFragment.getMapAsync(this);

        lockImage.setVisibility(View.VISIBLE);
        likeText.setText( m.likeCount);
        if(this.owner.uid.equals(userProfile.uid)) // own trip
        {
            Drawable d = getResources().getDrawable(R.drawable.like_full);
            likeImage.setImageDrawable(d);
            if(m.isPrivate)
            {
                Drawable dLock = getResources().getDrawable(R.drawable.lockclose);
                lockImage.setImageDrawable(dLock);
            }
            else
            {
                Drawable dLock = getResources().getDrawable(R.drawable.lockopen);
                lockImage.setImageDrawable(dLock);
            }
            lockImage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.d(TAG,"---Click lock");
                    ClickLock();
                }
            });
        }
        else // friend trip
        {
            lockImage.setVisibility(View.GONE);
            if(m.likesUID == null )
            {
                m.likesUID = new ArrayList<>();
                Drawable d = getResources().getDrawable(R.drawable.like_empty);
                likeImage.setImageDrawable(d);
            }
            else if(m.likesUID.contains(userProfile.uid))
            {
                Drawable d = getResources().getDrawable(R.drawable.like_full);
                likeImage.setImageDrawable(d);
            }
            else
            {
                Drawable d = getResources().getDrawable(R.drawable.like_empty);
                likeImage.setImageDrawable(d);
            }
            likeImage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ClickLike();
                }
            });

        }
    }
    private void ClickLike()
    {
        MyTrip m = owner.trips.get(index);
        if(m.likesUID == null)
        {
            m.likesUID = new ArrayList<>();
        }
        else if(m.likesUID.contains(userProfile.uid))
        {
            Drawable d = getResources().getDrawable(R.drawable.like_empty);
            likeImage.setImageDrawable(d);
            Integer likes = Integer.parseInt(m.likeCount);
            likes -=1;
            m.likeCount = likes.toString();
            m.likesUID.remove(userProfile.uid);
        }
        else
        {
            Drawable d = getResources().getDrawable(R.drawable.like_full);
            likeImage.setImageDrawable(d);
            Integer likes = Integer.parseInt(m.likeCount);
            likes +=1;
            m.likeCount = likes.toString();
            m.likesUID.add(userProfile.uid);
        }
        Log.d(TAG,"Click Like count : " + m.likeCount);
        owner.SaveProfile();
        likeText.setText( m.likeCount);
    }
    private void ClickLock()
    {
        MyTrip m = owner.trips.get(index);
        if(m.isPrivate)
        {
            m.isPrivate = false;
            owner.SaveProfile();
            Drawable dLock = getResources().getDrawable(R.drawable.lockopen);
            lockImage.setImageDrawable(dLock);
        }
        else
        {
            m.isPrivate = true;
            owner.SaveProfile();
            Drawable dLock = getResources().getDrawable(R.drawable.lockclose);
            lockImage.setImageDrawable(dLock);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setMinZoomPreference(8.0f);

        MyTrip m = owner.trips.get(index);
        Double longitude = m.listStages.get(0).longitude;
        Double latitude = m.listStages.get(0).latitude;

        LatLng coords = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
    }
}
