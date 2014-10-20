package net.gravitydevelopment.cnu.fragment;

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
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatButton;

import net.gravitydevelopment.cnu.CNU;
import net.gravitydevelopment.cnu.CNUApi;
import net.gravitydevelopment.cnu.CNULocationFeedItem;
import net.gravitydevelopment.cnu.CNULocationView;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;

import java.util.ArrayList;
import java.util.List;

public class LocationFeedFragment extends Fragment {

    private String mLocationName;
    private LinearLayout insertPoint;

    public LocationFeedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocationName = getArguments().getString(CNULocationView.ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_feed, container, false);
        Log.d(CNU.LOG_TAG, "root: " + rootView);
        insertPoint = (LinearLayout) rootView.findViewById(R.id.insertPoint);
        new Thread(new Runnable() {
            public void run() {
                updateFeed(CNUApi.getFeed(mLocationName));
            }
        }).start();

        return rootView;
    }

    public void updateFeed(final List<CNULocationFeedItem> items) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (items.size() > 0) {
                    insertPoint.findViewById(R.id.loadingText).setVisibility(View.INVISIBLE);
                    List<Button> list = new ArrayList<Button>();
                    for (CNULocationFeedItem item : items) {
                        Button button = new Button(getActivity());
                        if (item.isPinned()) {
                            Spanned text = Html.fromHtml(item.getMessage() + "<br><i>" + Util.minutesAgo(item.getTime()) + "</i>");
                            button.setText(text);
                            button.setEnabled(false);
                            list.add(0, button);
                        } else {
                            button = new FlatButton(getActivity());
                            Spanned text = Html.fromHtml("<strong>" + item.getMessage() + "</strong><br><i>" + Util.minutesAgo(item.getTime()) + "</i>");
                            button.setText(text);
                            button.setEnabled(false);
                            list.add(button);
                        }
                    }
                    for (Button button : list) {
                        insertPoint.addView(button);
                    }
                } else {
                    ((TextView)insertPoint.findViewById(R.id.loadingText)).setText("No recent updates");
                }
            }
        });
    }
}

