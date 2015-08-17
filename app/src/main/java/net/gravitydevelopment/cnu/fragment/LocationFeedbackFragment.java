package net.gravitydevelopment.cnu.fragment;

import com.cengalabs.flatui.FlatUI;
import com.cengalabs.flatui.views.FlatEditText;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import net.gravitydevelopment.cnu.DiningBuddy;
import net.gravitydevelopment.cnu.LocationActivity;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.modal.InfoItem;
import net.gravitydevelopment.cnu.modal.LocationItem;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;
import net.gravitydevelopment.cnu.service.SettingsService;

import java.util.ArrayList;

/**
 * Fragment that displays when a user reports on a feed for a location.
 */
public class LocationFeedbackFragment extends DialogFragment {

    private String mLocationName;

    public LocationFeedbackFragment() {

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
        final View rootView = inflater.inflate(R.layout.fragment_location_feedback, container, false);
        final FlatEditText feedbackBox = ((FlatEditText) rootView.findViewById(R.id.feedback));

        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                feedbackBox.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                feedbackBox.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
            }
        }, 100);

        final Spinner crowdedSpinner = (Spinner) rootView.findViewById(R.id.crowded_spinner);
        final Spinner minutesSpinner = (Spinner) rootView.findViewById(R.id.minutes_spinner);

        final ArrayList<String> crowded = InfoItem.CrowdedRating.getFeedbackList();
        ArrayAdapter<String> crowdedAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, crowded);
        crowdedSpinner.setAdapter(crowdedAdapter);

        ArrayList<String> minutes = new ArrayList<String>();
        for (int i = 0; i <= 10; i++) {
            String s = "" + i;
            if (i == 10)
                s += "+";
            s += " minute";
            if (i != 1)
                s += "s";
            minutes.add(s);
        }
        ArrayAdapter<String> minutesAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, minutes);
        minutesSpinner.setAdapter(minutesAdapter);

        rootView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = feedbackBox.getText().toString();
                if (feedback.equals("")) {
                    feedbackBox.getAttributes().setThemeSilent(FlatUI.BLOOD, DiningBuddy.getContext().getResources());
                    feedbackBox.onThemeChange();
                    return;
                }

                SettingsService settings = BackendService.getSettingsService();
                long lastUpdate = mLocationName.equals(Util.REGATTAS_NAME) ? settings.getLastFeedbackRegattas() : mLocationName.equals(Util.COMMONS_NAME) ? settings.getLastFeedbackCommons() : settings.getLastFeedbackEinsteins();
                if ((System.currentTimeMillis() - lastUpdate) > Util.MIN_FEEDBACK_INTERVAL) {
                    int crowdedValue = crowdedSpinner.getSelectedItemPosition();
                    int minuteValue = minutesSpinner.getSelectedItemPosition();
                    if (BackendService.isRunning()) {
                        LocationItem location = LocationService.getLastLocation();
                        LocationFeedFragment feed = (LocationFeedFragment) getParentFragment().getChildFragmentManager().findFragmentByTag("feedfragment");
                        BackendService.getLocationService()
                                .postFeedback(feed, mLocationName, location, crowdedValue,
                                        minuteValue, feedback, SettingsService.getUUID());
                        if (mLocationName.equals(Util.REGATTAS_NAME)) {
                            settings.setLastFeedbackRegattas(System.currentTimeMillis());
                        } else if (mLocationName.equals(Util.COMMONS_NAME)) {
                            settings.setLastFeedbackCommons(System.currentTimeMillis());
                        } else if (mLocationName.equals(Util.EINSTEINS_NAME)) {
                            settings.setLastFeedbackEinsteins(System.currentTimeMillis());
                        }
                    }
                    getDialog().dismiss();
                }
            }
        });

        getDialog().setTitle(R.string.feedback_title);

        return rootView;

    }

    public static LocationFeedbackFragment newInstance(String locationName) {
        LocationFeedbackFragment fragment = new LocationFeedbackFragment();
        Bundle args = new Bundle();
        args.putString(LocationActivity.ARG_NAME, locationName);
        fragment.setArguments(args);
        return fragment;
    }

}
