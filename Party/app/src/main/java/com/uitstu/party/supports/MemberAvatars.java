package com.uitstu.party.supports;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uitstu.party.MainActivity;
import com.uitstu.party.fragments.FragmentMap;
import com.uitstu.party.presenter.PartyFirebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Huy on 12/2/2016.
 */

public class MemberAvatars {
    private static Context activity;
    private static MemberAvatars mAvatars;
    public static MemberAvatars getInstant(Context activity){
        MemberAvatars.activity = activity;
        if (mAvatars == null){
            mAvatars = new MemberAvatars();
        }
        return mAvatars;
    }

    public static MemberAvatars getInstant(){
        /*
        if (mAvatars == null){
            mAvatars = new MemberAvatars();
        }
        */
        return mAvatars;
    }

    public static Map<String, Bitmap> memberAvatars;

    public MemberAvatars(){
        if (memberAvatars == null){
            memberAvatars = new HashMap<String, Bitmap>();
        }
    }

    public void putToList(String key, Bitmap bitmap){
        memberAvatars.put(key, bitmap);
    }

    public void putToList(final String key, final String URL){
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                Log.i("huyloadhinh","huyloadhinh da load: "+key+" ----- "+URL);
                if (memberAvatars.get(key) == null) {
                    memberAvatars.put(key, bitmap);
                }
                else {
                    memberAvatars.remove(key);
                    memberAvatars.put(key, bitmap);
                }
                FragmentMap.getInstant().updateMembers(PartyFirebase.users);
                //MainActivity.getInstant().updateMembers();
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        };

        if (!URL.equals(""))
            Picasso.with(activity).load(URL).into(target);


    }

    public static ArrayList<Target> targets;

    public void putToList(final String key, final String URL, Activity ac){
        if (targets == null) targets = new ArrayList<Target>();
        Target target;
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                Log.i("huyloadhinh","huyloadhinh da load1: "+key+" ----- "+URL);
                if (memberAvatars.get(key) == null) {
                    memberAvatars.put(key, bitmap);
                }
                else {
                    memberAvatars.remove(key);
                    memberAvatars.put(key, bitmap);
                }
                Log.i("huyload","huyload memberavatars size"+ PartyFirebase.users.size());
                FragmentMap.getInstant().updateMembers(PartyFirebase.users);
                //MainActivity.getInstant().updateMembers();
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        };
        targets.add(target);
        if (!URL.equals(""))
            Picasso.with(ac).load(URL).into(target);


    }

    public Bitmap getBitmap(String key){
        return memberAvatars.get(key);
    }

    public void remove(String key){
        memberAvatars.remove(key);
    }
}
