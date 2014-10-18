package net.gravitydevelopment.cnu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gravitydevelopment.cnu.R;

public class LocationHoursFragment extends Fragment {

    public LocationHoursFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_hours, container, false);
    }
}
