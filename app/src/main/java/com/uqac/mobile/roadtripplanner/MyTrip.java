package com.uqac.mobile.roadtripplanner;

import com.google.firebase.auth.FirebaseUser;

public class MyTrip {
    private static String TAG = "----------RoadTrip Planner-------------";
    public String ownerUID;
    //public String idTrip;
    public String tripName;
    public String longitude;
    public String latitude;
    public String likeCount;

    MyTrip(String uid,String tripName,String longitude,String latitude)
    {
        this.ownerUID = uid;
        this.tripName = tripName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.likeCount = Integer.toString(0);
    }
}
