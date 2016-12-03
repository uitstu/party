package com.uitstu.party.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.uitstu.party.R;
import com.uitstu.party.dialogfragments.FragmentEditInfo;
import com.uitstu.party.presenter.PartyFirebase;
import com.uitstu.party.services.MyService;
import com.uitstu.party.supports.RoundedImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static com.uitstu.party.presenter.PartyFirebase.user;

/**
 * Created by Huy on 11/6/2016.
 */

public class FragmentDrawer extends Fragment {

    private static FragmentDrawer fragmentDrawer;
    public static FragmentDrawer getInstant(){
        return fragmentDrawer;
    }

    public ImageView ivDrawerAvatar;
    TextView tvDrawerName, tvDrawerLocation, tvCurVelocity, tvVerhicle, tvEditInfo, tvLogout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentDrawer = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentDrawer = this;
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);

        tvDrawerName = (TextView)view.findViewById(R.id.tvDrawerName);
        tvDrawerLocation = (TextView)view.findViewById(R.id.tvDrawerLocation);
        tvCurVelocity = (TextView)view.findViewById(R.id.tvCurVelocity);
        tvVerhicle = (TextView)view.findViewById(R.id.tvVerhicle);
        tvEditInfo = (TextView)view.findViewById(R.id.tvEditInfo);
        tvLogout = (TextView)view.findViewById(R.id.tvLogout);

        ivDrawerAvatar = (ImageView)view.findViewById(R.id.ivDrawerAvatar);

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null && user.curPartyID != null) {
                    PartyFirebase.getInstant().firebaseDatabase.getReference().child("parties").child(user.curPartyID).child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("offline");
                }

                FirebaseAuth.getInstance().signOut();
                PartyFirebase.user = null;
                PartyFirebase.getInstant().removeFirebaseListener();

                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        tvEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new FragmentEditInfo();
                dialog.show(getChildFragmentManager(),"editing");
            }
        });

        return view;
    }

    public void updateUI(){

        String strEnter = "\n";

        if (user != null){
            if (user.name != null)
                tvDrawerName.setText(strEnter+"I'm "+user.name+"..."+strEnter);

            if (user.vehicle != null && user.vehicle.equals("car"))
                tvVerhicle.setText("I'm traveling by car..."+strEnter);
            else
                tvVerhicle.setText("I'm traveling by motorbike..."+strEnter);
        }

        tvCurVelocity.setText("\nCurrent velocity: Waiting"+strEnter);

        tvDrawerLocation.setText("My location is " + getAddress(getActivity(), MyService.lat, MyService.lng)+"..."+strEnter);
/*
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //StorageReference storageRef = storage.getReferenceFromUrl("gs://party2-949d9.appspot.com");
        //StorageReference pathReference = storageRef.child("avatars/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
        StorageReference pathReference = storage.getReferenceFromUrl(PartyFirebase.user.urlAvatar);

        Glide.clear(ivDrawerAvatar);
        Glide.with(getActivity().getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(ivDrawerAvatar);
*/
        //
        if (PartyFirebase.user != null && PartyFirebase.user.urlAvatar != null) {
            if (!PartyFirebase.user.urlAvatar.equals(""))
                Picasso.with(getActivity()).load(PartyFirebase.user.urlAvatar).into(ivDrawerAvatar);
        }



    }

    public String getAddress(Context context, double lat, double lng) {
        if (lat == 0.0 && lng == 0.0)
            return "My place: Waiting";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            String add = "";
            String str0, str1, str2, str3, str4;

            Address obj = addresses.get(0);
            str0 = obj.getAddressLine(0);
            str1 = obj.getAddressLine(1);
            str2 = obj.getAddressLine(2);
            str3 = obj.getAddressLine(3);
            str4 = obj.getAddressLine(4);

            if (str0 != null && !str0.equals(""))
                add += str0 + "\n";
            if (str1 != null && !str1.equals(""))
                add += str1 + " - ";
            if (str2 != null && !str2.equals(""))
                add += str2  + "\n";
            if (str3 != null && !str3.equals(""))
                add += str3 + " - ";
            if (str4 != null && !str4.equals(""))
                add += str4;

            for (int i = 0; i < addresses.size(); i++) {
                Log.d("=Adress=",addresses.get(i).toString() + " ------------ ");
            }


            return add;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateVelocity(float v){
        if (v>0.01)
            tvCurVelocity.setText("With velocity: "+Float.toString(v*(float)3.6) +" / "+MyService.maxSpeed*(float)3.6 +"(km/h)");
        else
            tvCurVelocity.setText("With velocity: Waiting");

        if (PartyFirebase.user != null && PartyFirebase.user.vehicle != null && !PartyFirebase.user.vehicle.equals("")) {
            if (PartyFirebase.user.vehicle.equals("car"))
                tvVerhicle.setText("I'm traveling by car...\n");
            else
                tvVerhicle.setText("I'm traveling by motorbike...\n");
        }

    }

    public Bitmap transformAvatar(Bitmap source) {
        try {

            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap
                    .createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                // source.recycle();
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    squaredBitmap.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            // canvas.drawArc(rectf, -90, 360, false, lightRed);
            // squaredBitmap.recycle();
            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return source;
    }

}
