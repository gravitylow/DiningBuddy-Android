package net.gravitydevelopment.cnu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

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
    private SettingsService settings;

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

        addTab(mTabHost, "graphfragment", "Activity", LocationGraphFragment.class, args);
        if (name.equals("Einsteins")) {
            addTab(mTabHost, "hoursfragment", "Hours", LocationHoursFragment.class, args);
        } else {
            addTab(mTabHost, "menufragment", "Menu", LocationMenuFragment.class, args);
        }
        addTab(mTabHost, "feedfragment", "Feed", LocationFeedFragment.class, args);

        settings = BackendService.getSettingsService();

        CNULocation location = LocationService.getLastLocation();
        Log.d(CNU.LOG_TAG, "Should show: " + shouldShowFeedback(location));
        if (shouldShowFeedback(location)) {
            addTab(mTabHost, "feedbackfragment", "Feedback", LocationFeedbackFragment.class, args);
            isShowingFeedback = true;
        }

        return rootView;
    }

    private void addTab(FragmentTabHost host, String tag, String indicator, Class clazz, Bundle arguments) {
        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        host.addTab(tabSpec, clazz, arguments);
    }

    public void updateLocation(CNULocation location) {
        if (shouldShowFeedback(location)) {
            if (!isShowingFeedback) {
                Log.d(CNU.LOG_TAG, "Adding tab ");
                addTab(mTabHost, "feedbackfragment", "Feedback", LocationFeedbackFragment.class, args);
                isShowingFeedback = true;
            }
        } else if (isShowingFeedback) {
            Log.d(CNU.LOG_TAG, "Removing tab ");
            mTabHost.setCurrentTab(mTabHost.getTabWidget().getTabCount() - 1);
            mTabHost.getTabWidget().removeView(mTabHost.getTabWidget().getChildTabViewAt(mTabHost.getTabWidget().getTabCount()));
            isShowingFeedback = false;
        }
    }

    private boolean shouldShowFeedback(CNULocation location) {
        if (location == null) {
            return false;
        }
        boolean add = false;
        if (location.getName().equals(name)) {
            long last = 0;
            if (name.equals(Util.REGATTAS_NAME)) {
                last = settings.getLastFeedbackRegattas();
            } else if (name.equals(Util.COMMONS_NAME)) {
                last = settings.getLastFeedbackCommons();
            } else if (name.equals(Util.EINSTEINS_NAME)) {
                last = settings.getLastFeedbackEinsteins();
            }
            add = last == -1 || last == 0 || System.currentTimeMillis() > Util.MIN_FEEDBACK_INTERVAL;
        }
        return add;
    }

    public static LocationMainFragment newInstance(String locationName) {
        LocationMainFragment fragment = new LocationMainFragment();
        Bundle args = new Bundle();
        args.putString(CNULocationView.ARG_NAME, locationName);
        fragment.setArguments(args);
        return fragment;
    }
}
