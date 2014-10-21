package net.gravitydevelopment.cnu.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
                        String message = Util.ellipsize(item.getMessage(), 25);
                        if (item.isPinned()) {
                            button = new FlatButton(getActivity());
                            Spanned text = Html.fromHtml("<strong>" + message + "</strong><br><i>" + Util.minutesAgo(item.getTime()) + "</i>");
                            button.setText(text);
                            list.add(0, button);

                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.popup_element);
                            final View layout = inflater.inflate(R.layout.popup_menu, viewGroup);
                            final PopupWindow window = new PopupWindow(getActivity());
                            window.setContentView(layout);
                            window.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            ((TextView)layout.findViewById(R.id.menuDescription)).setText(item.getDetail());
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    window.showAtLocation(layout, Gravity.CENTER, 0, 0);
                                }
                            });
                            ((Button)layout.findViewById(R.id.okButton)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    window.dismiss();
                                }
                            });
                        } else {
                            Spanned text = Html.fromHtml(message + "<br><i>" + Util.minutesAgo(item.getTime()) + "</i>");
                            button.setText(text);
                            list.add(button);

                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.popup_element);
                            final View layout = inflater.inflate(R.layout.popup_menu, viewGroup);
                            final PopupWindow window = new PopupWindow(getActivity());
                            window.setContentView(layout);
                            window.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            ((TextView)layout.findViewById(R.id.menuDescription)).setText(item.getMessage());
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    window.showAtLocation(layout, Gravity.CENTER, 0, 0);
                                }
                            });
                            ((Button)layout.findViewById(R.id.okButton)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    window.dismiss();
                                }
                            });
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

