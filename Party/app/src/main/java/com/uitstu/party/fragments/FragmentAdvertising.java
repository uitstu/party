package com.uitstu.party.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.uitstu.party.R;

/**
 * Created by Huy on 11/6/2016.
 */

public class FragmentAdvertising extends Fragment {
    private View rootView;
    WebView wvWeather;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_advertising, container, false);

        wvWeather = (WebView) rootView.findViewById(R.id.wvBlog);

        WebSettings webSettings = wvWeather.getSettings();
        webSettings.setJavaScriptEnabled(true);

        wvWeather.loadUrl("https://www.ivivu.com/blog/tag/phuot/");

        return rootView;
    }
}
