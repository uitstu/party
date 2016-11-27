package com.uitstu.party.presenter.interfaces;

import com.google.android.gms.maps.model.LatLng;
import com.uitstu.party.models.User;

import java.util.ArrayList;

/**
 * Created by Huy on 11/17/2016.
 */

public interface IUpdateMap {
    void updateMembers(ArrayList<User> users);
    void updatePath(ArrayList<LatLng> path);
}
