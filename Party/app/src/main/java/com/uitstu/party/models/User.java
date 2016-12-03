package com.uitstu.party.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Huy on 11/17/2016.
 */

public class User {
    public String UID;
    public String curPartyID;
    public String name;
    public Double latitude;
    public Double longitude;
    public Double maxVelocity;
    public String vehicle;
    public String urlAvatar;
    public String status;

    //public Bitmap avatar;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("UID", UID);
        result.put("curPartyID", curPartyID);
        result.put("name", name);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("maxVelocity", maxVelocity);
        result.put("vehicle", vehicle);
        result.put("urlAvatar", urlAvatar);
        result.put("status", status);

        return result;
    }

    public User(){
        UID = "";
        curPartyID = "";
        name = "";
        latitude = 0.0;
        longitude = 0.0;
        maxVelocity = 0.0;
        vehicle = "";
        urlAvatar = "";
    }

}
