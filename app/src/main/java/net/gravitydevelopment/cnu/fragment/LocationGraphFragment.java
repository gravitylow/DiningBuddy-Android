package net.gravitydevelopment.cnu.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.gravitydevelopment.cnu.LocationActivity;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.network.API;

public class LocationGraphFragment extends Fragment {

    private String mLocationName;

    public LocationGraphFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocationName = getArguments().getString(LocationActivity.ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_graph, container, false);
        WebView webView = (WebView) rootView.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(API.API_URL + "/graphs/" + mLocationName);
        webView.setScrollContainer(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setBackgroundColor(Color.parseColor("#DFDFD7"));
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        return rootView;
    }
}
