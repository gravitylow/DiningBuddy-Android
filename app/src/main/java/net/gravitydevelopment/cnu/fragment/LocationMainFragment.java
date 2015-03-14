package net.gravitydevelopment.cnu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import net.gravitydevelopment.cnu.DiningBuddy;
import net.gravitydevelopment.cnu.LocationActivity;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.modal.LocationItem;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;
import net.gravitydevelopment.cnu.service.SettingsService;

public class LocationMainFragment extends Fragment {

    private FragmentTabHost mTabHost;
    private String mLocationName;
    private Bundle mArgs;
    private SettingsService mSettings;

    public LocationMainFragment() {

    }

    public static LocationMainFragment newInstance(String locationName) {
        LocationMainFragment fragment = new LocationMainFragment();
        Bundle args = new Bundle();
        args.putString(LocationActivity.ARG_NAME, locationName);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs = getArguments();
        this.mLocationName = mArgs.getString(LocationActivity.ARG_NAME);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_main, container, false);
        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

       addTab(mTabHost, "feedfragment", "Feed", LocationFeedFragment.class, mArgs);
        if (mLocationName.equals("Einsteins")) {
            addTab(mTabHost, "hoursfragment", "Hours", LocationHoursFragment.class, mArgs);
        } else {
            addTab(mTabHost, "menufragment", "Menu", LocationMenuFragment.class, mArgs);
        }
        addTab(mTabHost, "graphfragment", "Activity", LocationGraphFragment.class, mArgs);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("feedfragment")) {
                    ((LocationFeedFragment) getChildFragmentManager().findFragmentByTag("feedfragment")).checkFeedback();
                }
            }
        });

        mSettings = BackendService.getSettingsService();
        return rootView;
    }

    private void addTab(FragmentTabHost host, String tag, String indicator, Class clazz, Bundle arguments) {
        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        host.addTab(tabSpec, clazz, arguments);
    }

    public void notifyFeedbackSubmitted() {
        ((LocationFeedFragment) getChildFragmentManager().findFragmentByTag("feedfragment")).onFeedbackSubmitted();
    }

    public void updateLocation(LocationItem location) {
    }
}
