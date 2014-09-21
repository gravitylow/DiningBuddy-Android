package net.gravitydevelopment.cnu.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.gravitydevelopment.cnu.CNUApi;
import net.gravitydevelopment.cnu.CNUViewLocation;
import net.gravitydevelopment.cnu.R;

public class LocationGraphFragment extends Fragment {

    private String mLocationName;

    public LocationGraphFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocationName = getArguments().getString(CNUViewLocation.ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_graph, container, false);
        WebView webView = (WebView) rootView.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(CNUApi.getApiUrl() + "graphs/" + mLocationName);

        return rootView;
    }

    public static LocationGraphFragment newInstance(String locationName) {
        LocationGraphFragment fragment = new LocationGraphFragment();
        Bundle args = new Bundle();
        args.putString(CNUViewLocation.ARG_NAME, locationName);
        fragment.setArguments(args);
        return fragment;
    }
}
