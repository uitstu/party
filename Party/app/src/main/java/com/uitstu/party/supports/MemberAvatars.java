package com.uitstu.party.supports;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uitstu.party.fragments.FragmentMap;
import com.uitstu.party.presenter.PartyFirebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Huy on 12/2/2016.
 */

public class MemberAvatars {
    private static Activity activity;
    private static MemberAvatars mAvatars;
    public static MemberAvatars getInstant(Activity activity){
        MemberAvatars.activity = activity;
        if (mAvatars == null){
            mAvatars = new MemberAvatars();
        }
        return mAvatars;
    }

    public static MemberAvatars getInstant(){
        if (mAvatars == null){
            mAvatars = new MemberAvatars();
        }
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

    public void putToList(final String key, String URL){
        Picasso.with(activity).load(URL).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                if (memberAvatars.get(key) == null) {
                    memberAvatars.put(key, bitmap);
                }
                else {
                    memberAvatars.remove(key);
                    memberAvatars.put(key, bitmap);
                }
                FragmentMap.getInstant().updateMembers(PartyFirebase.users);
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        });

    }

    public Bitmap getBitmap(String key){
        return memberAvatars.get(key);
    }

    public void remove(String key){
        memberAvatars.remove(key);
    }
}
