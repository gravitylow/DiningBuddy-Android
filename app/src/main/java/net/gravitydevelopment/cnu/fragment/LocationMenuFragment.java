package net.gravitydevelopment.cnu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import net.gravitydevelopment.cnu.API;
import net.gravitydevelopment.cnu.DiningBuddy;
import net.gravitydevelopment.cnu.CNULocationMenuItem;
import net.gravitydevelopment.cnu.LocationActivity;
import net.gravitydevelopment.cnu.R;

import java.util.List;

public class LocationMenuFragment extends Fragment {

    private String mLocationName;
    private LinearLayout mInsertPoint;

    public LocationMenuFragment() {

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
        View rootView = inflater.inflate(R.layout.fragment_location_menu, container, false);
        Log.d(DiningBuddy.LOG_TAG, "root: " + rootView);
        mInsertPoint = (LinearLayout) rootView.findViewById(R.id.insertPoint);
        new Thread(new Runnable() {
            public void run() {
                updateMenu(API.getMenu(mLocationName));
            }
        }).start();

        return rootView;
    }

    public void updateMenu(final List<CNULocationMenuItem> items) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInsertPoint.findViewById(R.id.loadingText).setVisibility(View.INVISIBLE);
                for (CNULocationMenuItem item : items) {
                    final Button button = new Button(getActivity());
                    Spanned text = Html.fromHtml("<strong>" + item.getStartTime() + " - " + item.getEndTime() + "</strong><br>" + item.getSummary());
                    button.setText(text);
                    mInsertPoint.addView(button);

                    final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage(item.getDescription())
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                            .create();
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.show();
                        }
                    });
                }
            }
        });
    }
}
