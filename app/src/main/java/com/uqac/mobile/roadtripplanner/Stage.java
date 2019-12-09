package com.uqac.mobile.roadtripplanner;

public class Stage {
    public double longitude;
    public double latitude;
    public String startDate;
    public String endDate;
    public String name;

    public Stage(String name ,double la,double lo,String startDate,String endDate)
    {
        this.name = name;
        longitude = lo;
        latitude = la;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
