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

    public void setName(String name) {
        this.name = name;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
