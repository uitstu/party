package com.uitstu.party.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.uitstu.party.R;
import com.uitstu.party.dialogfragments.FragmentAnchor;
import com.uitstu.party.dialogfragments.FragmentCreateParty;
import com.uitstu.party.dialogfragments.FragmentJoinParty;
import com.uitstu.party.dialogfragments.FragmentOutParty;
import com.uitstu.party.models.Anchor;
import com.uitstu.party.models.User;
import com.uitstu.party.presenter.PartyFirebase;
import com.uitstu.party.presenter.interfaces.IUpdateMap;
import com.uitstu.party.services.MyService;
import com.uitstu.party.supports.MemberAvatars;

import java.util.ArrayList;

/**
 * Created by Huy on 11/6/2016.
 */

public class FragmentMap extends Fragment implements OnMapReadyCallback, IUpdateMap {

    private static FragmentMap fragmentMap;
    public static FragmentMap getInstant(){
        return fragmentMap;
    }

    private View rootView;
    GoogleMap googleMap;
    MapView mapView;
    SupportPlaceAutocompleteFragment edtCompletePlace;

    FloatingActionButton fabAnchor, fabCreate, fabJoin, fabOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentMap = this;
        PartyFirebase.map = this;

        try {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
            MapsInitializer.initialize(this.getActivity());
            mapView = (MapView) rootView.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        } catch (Exception e) {
        }

        fabAnchor = (FloatingActionButton) rootView.findViewById(R.id.fabAnchor);
        fabCreate = (FloatingActionButton) rootView.findViewById(R.id.fabCreate);
        fabJoin = (FloatingActionButton) rootView.findViewById(R.id.fabJoin);
        fabOut = (FloatingActionButton) rootView.findViewById(R.id.fabOut);

        fabAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new FragmentAnchor();
                dialog.show(getChildFragmentManager(),"dropping");
            }
        });

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new FragmentCreateParty();
                dialog.show(getChildFragmentManager(),"creating");
            }
        });

        fabJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new FragmentJoinParty();
                dialog.show(getChildFragmentManager(),"joining");
            }
        });

        fabOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new FragmentOutParty();
                dialog.show(getChildFragmentManager(),"outting");
            }
        });

        edtCompletePlace = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.edtCompletePlace);

        edtCompletePlace.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

            }

            @Override
            public void onError(Status status) {

            }
        });


        //
        /*
        try {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(MyService.lat, MyService.lng), 10);
            googleMap.moveCamera(cameraUpdate);
        }
        catch (Exception e){

        }
        */

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(MyService.lat, MyService.lng), 14);
        googleMap.moveCamera(cameraUpdate);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

    }

    @Override
    public void updateMembers(ArrayList<User> users) {
        googleMap.clear();

        ArrayList<MarkerOptions> markerOptionses = new ArrayList<MarkerOptions>();

        for (int i=0; i<users.size(); i++){
            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(users.get(i).UID))
                continue;
            LatLng latLng = new LatLng(users.get(i).latitude, users.get(i).longitude);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            if (users.get(i).status != null && users.get(i).equals("online")) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(overlayMapIcon(MemberAvatars.getInstant().getBitmap(users.get(i).UID),true)));
            }
            else{
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(overlayMapIcon(MemberAvatars.getInstant().getBitmap(users.get(i).UID),false)));
            }

            markerOptionses.add(markerOptions);

            googleMap.addMarker(markerOptions);
        }

        updateAnchor();

    }

    public void updateAnchor(){
        if (PartyFirebase.anchor != null && (!PartyFirebase.anchor.latitude.equals(0.0) || !PartyFirebase.anchor.longitude.equals(0.0))){
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(PartyFirebase.anchor.latitude, PartyFirebase.anchor.longitude);
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.anchor));
            googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void updatePath(ArrayList<LatLng> path) {
        Toast.makeText(getActivity(),"hi", Toast.LENGTH_SHORT).show();
    }

    public Bitmap overlayMapIcon(Bitmap icon, boolean isOnline) {
        //style of marker
        Bitmap styleMarker;
        if (isOnline){
            styleMarker = BitmapFactory.decodeResource(getResources(),R.drawable.m_blue);
        }
        else{
            styleMarker = BitmapFactory.decodeResource(getResources(),R.drawable.m_red);
        }
        styleMarker = Bitmap.createScaledBitmap(styleMarker, 100,100,false);
        if (icon != null) {
            //overlay will combine between style and true icon
            Bitmap overlay = Bitmap.createBitmap(styleMarker.getWidth(), styleMarker.getHeight(), styleMarker.getConfig());
            icon = Bitmap.createScaledBitmap(icon, 52,52,false);
            Canvas canvas = new Canvas(overlay);
            //canvas.drawBitmap(styleMarker, new Matrix(), null);

            //fit per pixel follow on TYPE
            canvas.drawBitmap(icon, 25/*styleMarker.getWidth()*/, 13/*styleMarker.getHeight()*/, null);
            canvas.drawBitmap(styleMarker, new Matrix(), null);
            return overlay;
        } else
            return styleMarker;
    }

    public void updateFabs(boolean hasParty){
        if (hasParty)
            fabOut.setVisibility(View.VISIBLE);
        else
            fabOut.setVisibility(View.INVISIBLE);
    }

}
