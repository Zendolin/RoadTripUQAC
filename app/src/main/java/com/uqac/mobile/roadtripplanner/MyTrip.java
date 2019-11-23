package com.uqac.mobile.roadtripplanner;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTrip {
    private static String TAG = "----------RoadTrip Planner-------------";
    public String ownerUID;
    public String tripName;
    public String likeCount;
    public ArrayList<Stage> listStages;
    public String startDate;
    public String endDate;

    MyTrip(String uid,String tripName,String startDate,String endDate,String likeCount)
    {
        listStages = new ArrayList<>();
        this.ownerUID = uid;
        this.tripName = tripName;
        this.likeCount = likeCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
