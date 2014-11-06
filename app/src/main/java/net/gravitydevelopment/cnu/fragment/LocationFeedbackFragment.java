package net.gravitydevelopment.cnu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import net.gravitydevelopment.cnu.CNULocationView;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;
import net.gravitydevelopment.cnu.service.SettingsService;

import java.util.ArrayList;

public class LocationFeedbackFragment extends Fragment {

    private String name;
    private boolean submitted;

    public LocationFeedbackFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(CNULocationView.ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_location_feedback, container, false);
        Spinner crowdedSpinner = (Spinner) rootView.findViewById(R.id.crowded_spinner);
        final ArrayList<String> crowded = CNULocationInfo.CrowdedRating.getFeedbackList();
        ArrayAdapter<String> crowdedAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, crowded);
        crowdedSpinner.setAdapter(crowdedAdapter);

        Spinner minutesSpinner = (Spinner) rootView.findViewById(R.id.minutes_spinner);
        ArrayList<String> minutes = new ArrayList<String>();
        for (int i=0;i<=10;i++) {
            String s = "" + i;
            if (i == 10) s += "+";
            s += " minute";
            if (i != 1) s += "s";
            minutes.add(s);
        }
        ArrayAdapter<String> minutesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, minutes);
        minutesSpinner.setAdapter(minutesAdapter);

        if (submitted) {
            setSubmitted(rootView);
        } else {
            rootView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingsService settings = BackendService.getSettingsService();
                    long lastUpdate = name.equals(Util.REGATTAS_NAME) ? settings.getLastFeedbackRegattas() : settings.getLastFeedbackCommons();
                    if ((System.currentTimeMillis() - lastUpdate) > Util.MIN_FEEDBACK_INTERVAL) {
                        int crowdedValue = ((Spinner) rootView.findViewById(R.id.crowded_spinner)).getSelectedItemPosition();
                        int minuteValue = ((Spinner) rootView.findViewById(R.id.minutes_spinner)).getSelectedItemPosition();
                        String feedback = ((EditText) rootView.findViewById(R.id.feedback)).getText().toString();
                        if (BackendService.isRunning()) {
                            CNULocation location = LocationService.getLastLocation();
                            BackendService.getLocationService().postFeedback(name, location, crowdedValue, minuteValue, feedback, SettingsService.getUUID());
                            if (name.equals(Util.REGATTAS_NAME)) {
                                settings.setLastFeedbackRegattas(System.currentTimeMillis());
                            } else if (name.equals(Util.COMMONS_NAME)) {
                                settings.setLastFeedbackCommons(System.currentTimeMillis());
                            } else if (name.equals(Util.EINSTEINS_NAME)) {
                                settings.setLastFeedbackEinsteins(System.currentTimeMillis());
                            }
                        }
                        setSubmitted(rootView);
                    }
                }
            });
        }

        return rootView;

    }

    private void setSubmitted(View rootView) {
        submitted = true;
        rootView.findViewById(R.id.crowded_text).setVisibility(View.GONE);
        rootView.findViewById(R.id.crowded_spinner).setVisibility(View.GONE);
        rootView.findViewById(R.id.minutes_text).setVisibility(View.GONE);
        rootView.findViewById(R.id.minutes_spinner).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_text).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback).setVisibility(View.GONE);
        rootView.findViewById(R.id.submit).setVisibility(View.GONE);
        rootView.findViewById(R.id.feedback_response_text).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.feedback_response_text_detail).setVisibility(View.VISIBLE);
    }


    public static LocationFeedbackFragment newInstance(String name) {
        LocationFeedbackFragment fragment = new LocationFeedbackFragment();
        Bundle args = new Bundle();
        args.putString(CNULocationView.ARG_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

}
