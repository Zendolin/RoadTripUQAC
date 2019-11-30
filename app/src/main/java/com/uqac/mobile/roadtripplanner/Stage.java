package com.uqac.mobile.roadtripplanner;

public class Stage {
    public double longitude;
    public double latitude;
    public String startDate;
    public String endDate;

    public Stage(double la,double lo,String startDate,String endDate)
    {
        //place = p;
        longitude = lo;
        latitude = la;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
