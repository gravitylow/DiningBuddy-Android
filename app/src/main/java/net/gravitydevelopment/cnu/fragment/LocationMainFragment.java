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
    private boolean mIsShowingFeedback;
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

        addTab(mTabHost, "graphfragment", "Activity", LocationGraphFragment.class, mArgs);
        if (mLocationName.equals("Einsteins")) {
            addTab(mTabHost, "hoursfragment", "Hours", LocationHoursFragment.class, mArgs);
        } else {
            addTab(mTabHost, "menufragment", "Menu", LocationMenuFragment.class, mArgs);
        }
        addTab(mTabHost, "feedfragment", "Feed", LocationFeedFragment.class, mArgs);

        mSettings = BackendService.getSettingsService();

        LocationItem location = LocationService.getLastLocation();
        Log.d(DiningBuddy.LOG_TAG, "Should show: " + shouldShowFeedback(location));
        if (shouldShowFeedback(location)) {
            addTab(mTabHost, "feedbackfragment", "Feedback", LocationFeedbackFragment.class, mArgs);
            mIsShowingFeedback = true;
        }

        return rootView;
    }

    private void addTab(FragmentTabHost host, String tag, String indicator, Class clazz, Bundle arguments) {
        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        host.addTab(tabSpec, clazz, arguments);
    }

    public void updateLocation(LocationItem location) {
        if (shouldShowFeedback(location)) {
            if (!mIsShowingFeedback) {
                Log.d(DiningBuddy.LOG_TAG, "Adding tab ");
                addTab(mTabHost, "feedbackfragment", "Feedback", LocationFeedbackFragment.class, mArgs);
                mIsShowingFeedback = true;
            }
        } else if (mIsShowingFeedback) {
            Log.d(DiningBuddy.LOG_TAG, "Removing tab ");
            mTabHost.setCurrentTab(mTabHost.getTabWidget().getTabCount() - 1);
            mTabHost.getTabWidget().removeView(mTabHost.getTabWidget().getChildTabViewAt(mTabHost.getTabWidget().getTabCount()));
            mIsShowingFeedback = false;
        }
    }

    private boolean shouldShowFeedback(LocationItem location) {
        /*if (location == null) {
            return false;
        }
        boolean add = false;
        if (location.getName().equals(mLocationName)) {
            long last = 0;
            switch (mLocationName) {
                case Util.REGATTAS_NAME:
                    last = mSettings.getLastFeedbackRegattas();
                    break;
                case Util.COMMONS_NAME:
                    last = mSettings.getLastFeedbackCommons();
                    break;
                case Util.EINSTEINS_NAME:
                    last = mSettings.getLastFeedbackEinsteins();
                    break;
            }
            add = last == -1 || last == 0 || System.currentTimeMillis() > Util.MIN_FEEDBACK_INTERVAL;
        }
        return add;*/
        return false;
    }
}
