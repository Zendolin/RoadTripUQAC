package com.uqac.mobile.roadtripplanner;


import java.util.ArrayList;

public class MyTrip {
    private static String TAG = "----------RoadTrip Planner-------------";
    public String ownerUID;
    public String tripName;
    public String likeCount;
    public ArrayList<String> likesUID;
    public ArrayList<Stage> listStages;
    public String startDate;
    public String endDate;

    public MyTrip(String uid,String tripName,String startDate,String endDate,String likeCount,ArrayList likes)
    {
        listStages = new ArrayList<>();
        likesUID = new ArrayList<>();
        this.ownerUID = uid;
        this.tripName = tripName;
        this.likeCount = likeCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.likesUID = likes;
    }

}
