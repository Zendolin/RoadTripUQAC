package com.uqac.mobile.roadtripplanner;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

public class MyTrip {
    private static String TAG = "----------RoadTrip Planner-------------";
    public FirebaseUser user;
    public String ownerUID;
    public String tripName;
    public String likeCount;
    public String mainLocation;

    MyTrip(FirebaseUser user,String tripName,String mainLocation)
    {
        this.user = user;
        this.ownerUID = user.getUid();
        this.tripName = tripName;
        this.likeCount = Integer.toString(0);
        this.mainLocation = mainLocation;
    }
}
