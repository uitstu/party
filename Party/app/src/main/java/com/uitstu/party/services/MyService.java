package com.uitstu.party.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.uitstu.party.MainActivity;
import com.uitstu.party.fragments.FragmentDrawer;
import com.uitstu.party.fragments.FragmentMap;
import com.uitstu.party.models.Tracking;
import com.uitstu.party.presenter.PartyFirebase;

import org.w3c.dom.Text;

public class MyService extends Service {
    public static MyService getMyService;
    public static Activity activity;
    public static Vibrator vibrator;
    private LocationManager locationManager;
    public static MediaPlayer mediaPlayer;

    public static Double lat = 0.0;
    public static Double lng = 0.0;
    public static float velocity;

    public static float maxSpeed = 20.0f;
    public static float curSpeed = 0.0f;
    private TextView textView;

    public MyService() {
    }

    public MyService(Activity refActivity) {
        activity = refActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getMyService = this;

        if (vibrator == null){
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }

        if (locationManager == null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3,
                    3,
                    new MyLocationListener());
        }

        //huy lock
        /*
        if (MyService.mediaPlayer == null){
            MyService.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.baodong);

            //MyService.mediaPlayer.start();

            MyService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (MyService.curSpeed >= MyService.maxSpeed) {
                        MyService.mediaPlayer.start();
                        MyService.vibrator.vibrate(900);
                        Log.i("dungsai","dung");
                    }
                    else {
                        Log.i("dungsai","sai");
                    }
                }
            });
        }
        */

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //huy lock
        /*
        if (MyService.mediaPlayer == null) {
            MyService.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.baodong);
        }

        try {
            textView = (TextView) activity.findViewById(R.id.tvSpeed);
            textView.setText("wait for GPS...");
        }
        catch (Exception ex){

        }
        */
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

        //MyService.vibrator.cancel();

        MyService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });

        MyService.mediaPlayer.release();
        MyService.mediaPlayer = null;

        super.onDestroy();
    }

    private class MyLocationListener implements LocationListener
    {
        public MyLocationListener() {

        }

        @Override
        public void onLocationChanged(Location location)
        {
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                PartyFirebase.getInstant().updateOnlineStatus();

            if (PartyFirebase.user != null && FirebaseAuth.getInstance().getCurrentUser() != null){
                PartyFirebase.user.latitude = location.getLatitude();
                PartyFirebase.user.longitude = location.getLongitude();
                PartyFirebase.getInstant().updateUserDataToFirebase();
            }

            lat = location.getLatitude();
            lng = location.getLongitude();
            velocity = location.getSpeed();

            if (FragmentDrawer.getInstant() != null)
                FragmentDrawer.getInstant().updateVelocity(velocity);

            try{
                if (Tracking.getInstant().isTracking)
                    FragmentMap.getInstant().updateMembers(PartyFirebase.users);
            }
            catch (Exception e){

            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle b) {}

        @Override
        public void onProviderDisabled(String s) {}

        @Override
        public void onProviderEnabled(String s) {}

    }
}