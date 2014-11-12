package net.gravitydevelopment.cnu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gravitydevelopment.cnu.CNU;
import net.gravitydevelopment.cnu.CNULocationView;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;
import net.gravitydevelopment.cnu.service.SettingsService;

public class LocationMainFragment extends Fragment {

    private FragmentTabHost mTabHost;
    private String name;
    private Bundle args;
    private boolean isShowingFeedback;

    public LocationMainFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
        this.name = args.getString(CNULocationView.ARG_NAME);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_main, container, false);
        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("graphfragment").setIndicator("Activity"), LocationGraphFragment.class, args);
        if (name.equals("Einsteins")) {
            mTabHost.addTab(mTabHost.newTabSpec("hoursfragment").setIndicator("Hours"), LocationHoursFragment.class, args);
        } else {
            mTabHost.addTab(mTabHost.newTabSpec("menufragment").setIndicator("Menu"), LocationMenuFragment.class, args);
        }
        mTabHost.addTab(mTabHost.newTabSpec("feedfragment").setIndicator("Feed"), LocationFeedFragment.class, args);

        CNULocation location = LocationService.getLastLocation();
        SettingsService settings = BackendService.getSettingsService();
        if (settings != null) {
            long lastUpdate = name.equals(Util.REGATTAS_NAME) ? settings.getLastFeedbackRegattas() : name.equals(Util.COMMONS_NAME) ? settings.getLastFeedbackCommons() : settings.getLastFeedbackEinsteins();
            if (location != null && location.getName().equals(name)) {
                if ((System.currentTimeMillis() - lastUpdate) > Util.MIN_FEEDBACK_INTERVAL) {
                    mTabHost.addTab(mTabHost.newTabSpec("feedbackfragment").setIndicator("Feedback"), LocationFeedbackFragment.class, args);
                    isShowingFeedback = true;
                }
            }
        }


        return rootView;
    }

    public void updateLocation(CNULocation location) {
        Log.d(CNU.LOG_TAG, "Is showing feedback: " + isShowingFeedback);
        if (location != null && location.getName().equals(name)) {
            if (!isShowingFeedback) {
                Log.d(CNU.LOG_TAG, "Adding tab ");
                mTabHost.addTab(mTabHost.newTabSpec("feedbackfragment").setIndicator("Feedback"), LocationFeedbackFragment.class, args);
                isShowingFeedback = true;
            }
        } else if (isShowingFeedback) {
            Log.d(CNU.LOG_TAG, "Removing tab ");
            mTabHost.setCurrentTab(mTabHost.getTabWidget().getTabCount() - 1);
            mTabHost.getTabWidget().removeView(mTabHost.getTabWidget().getChildTabViewAt(mTabHost.getTabWidget().getTabCount()));
            isShowingFeedback = false;
        }
    }

    public static LocationMainFragment newInstance(String locationName) {
        LocationMainFragment fragment = new LocationMainFragment();
        Bundle args = new Bundle();
        args.putString(CNULocationView.ARG_NAME, locationName);
        fragment.setArguments(args);
        return fragment;
    }
}
