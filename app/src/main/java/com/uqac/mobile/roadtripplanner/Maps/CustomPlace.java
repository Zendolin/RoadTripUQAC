package com.uqac.mobile.roadtripplanner.Maps;

public class CustomPlace implements Comparable<CustomPlace> {

    private String placeId;
    private Integer step;

    public CustomPlace(String placeId, int step) {
        this.placeId = placeId;
        this.step = step;
    }

    public CustomPlace(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public int compareTo(CustomPlace customPlace) {
        if (getStep() == null || customPlace.getStep() == null) {
            return 0;
        }
        return getStep().compareTo(customPlace.getStep());
    }
}
