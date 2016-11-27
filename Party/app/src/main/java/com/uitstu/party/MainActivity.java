package com.uitstu.party;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.uitstu.party.adapters.AdapterViewPager;
import com.uitstu.party.dialogfragments.FragmentEditInfo;
import com.uitstu.party.fragments.FragmentAdvertising;
import com.uitstu.party.fragments.FragmentChatting;
import com.uitstu.party.fragments.FragmentDrawer;
import com.uitstu.party.fragments.FragmentMap;
import com.uitstu.party.presenter.PartyFirebase;
import com.uitstu.party.services.MyService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomBar bottomBar;
    ViewPager viewPager;
    List<Fragment> lstFragments;

    FragmentMap fragmentMap;
    FragmentChatting fragmentChatting;
    FragmentAdvertising fragmentAdvertising;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setResult(LoginActivity.RESULT_NORMAL);

        mapViews();

        lstFragments = new ArrayList<Fragment>();
        fragmentMap = new FragmentMap();
        fragmentChatting = new FragmentChatting();
        fragmentAdvertising = new FragmentAdvertising();

        lstFragments.add(fragmentMap);
        lstFragments.add(fragmentChatting);
        lstFragments.add(fragmentAdvertising);

        AdapterViewPager adapterViewPager = new AdapterViewPager(getSupportFragmentManager(), lstFragments);
        viewPager.setAdapter(adapterViewPager);

        setEvents();

        MyService.activity = this;
        Intent in = new Intent(MainActivity.this, MyService.class);
        startService(in);
    }

    void mapViews(){
        drawer = (DrawerLayout) findViewById(R.id.drawer) ;
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    void setEvents() {
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId){
                    case R.id.tab_map:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_chatting:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_advertising:
                        viewPager.setCurrentItem(2);
                        break;
                    default:
                        viewPager.setCurrentItem(0);
                        break;
                }

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomBar.selectTabAtPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                0,
                0
        ) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                FragmentDrawer.getInstant().updateUI();

            }
        };
        drawer.addDrawerListener(mDrawerToggle);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}