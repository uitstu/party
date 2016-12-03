package com.uitstu.party.models;

import com.google.android.gms.maps.model.MarkerOptions;
import com.uitstu.party.fragments.FragmentMap;
import com.uitstu.party.presenter.PartyFirebase;

/**
 * Created by Huy on 12/3/2016.
 */

public class Tracking {
    public static final int ANCHOR = 0;
    public static final int MEMBER = 1;
    public static final int PLACE = 2;

    public static Anchor anchor;
    public static MarkerOptions placeFounded;
    public static User user;

    private static Tracking tracking;
    public static Tracking getInstant(){
        if (tracking == null)
            tracking = new Tracking();
        return tracking;
    }

    public int type;
    public Double latitude;
    public Double longitude;
    public boolean isTracking;

    public Tracking(){
        latitude = 0.0;
        longitude = 0.0;
        type = PLACE;
        isTracking = false;
        anchor = PartyFirebase.anchor;
        placeFounded = FragmentMap.placeFounded;
    }

    public void setLatLng(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setIsTracking(boolean isTracking){
        this.isTracking = isTracking;
    }
}
