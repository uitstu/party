package com.uitstu.party;

import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.uitstu.party.adapters.AdapterViewPager;
import com.uitstu.party.fragments.FragmentAdvertising;
import com.uitstu.party.fragments.FragmentChatting;
import com.uitstu.party.fragments.FragmentMap;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomBar bottomBar;
    ViewPager viewPager;
    List<Fragment> lstFragments;

    FragmentMap fragmentMap;
    FragmentChatting fragmentChatting;
    FragmentAdvertising fragmentAdvertising;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    void mapViews(){
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
    }
}
