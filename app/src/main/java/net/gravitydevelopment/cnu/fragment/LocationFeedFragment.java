package net.gravitydevelopment.cnu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import net.gravitydevelopment.cnu.CNUApi;
import net.gravitydevelopment.cnu.CNULocationFeedItem;
import net.gravitydevelopment.cnu.CNULocationView;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;

import java.util.ArrayList;
import java.util.List;

public class LocationFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String mLocationName;
    private TableLayout tableLayout;
    private SwipeRefreshLayout refreshLayout;

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
        tableLayout = (TableLayout) rootView.findViewById(R.id.table);
        new Thread(new Runnable() {
            public void run() {
                updateFeed(CNUApi.getFeed(mLocationName));
            }
        }).start();

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.view_feed_refresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                R.color.orange_primary,
                R.color.sea_primary,
                R.color.sky_primary,
                R.color.grass_primary);

        return rootView;
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            public void run() {
                updateFeed(CNUApi.getFeed(mLocationName));
            }
        }).start();
    }

    public void updateFeed(final List<CNULocationFeedItem> items) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (items.size() > 0) {
                    List<TableRow> list = new ArrayList<TableRow>();
                    int i = 0;
                    for (CNULocationFeedItem item : items) {
                        getView().findViewById(R.id.loadingText).setVisibility(View.INVISIBLE);
                        String message = item.getMessage();
                        TableRow row = new TableRow(getActivity());
                        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));
                        TextView text = new TextView(getActivity());
                        row.addView(text);

                        if (item.isPinned()) {
                            row.setBackgroundColor(Color.parseColor("#2ab081"));
                            Spanned value = Html.fromHtml("<strong>" + message + "</strong><br><i>" + Util.minutesAgo(item.getTime()) + "</i>");
                            text.setText(value);
                            list.add(0, row);

                            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                    .setMessage(item.getDetail())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    })
                                    .create();
                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.show();
                                }
                            });
                        } else {
                            i++;
                            if (i % 2 == 0) {
                                row.setBackgroundColor(Color.parseColor("#E3E3DC"));
                            }
                            Spanned value = Html.fromHtml(message + "<br><i>" + Util.minutesAgo(item.getTime()) + "</i>");
                            text.setText(value);
                            list.add(row);

                            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                    .setMessage(item.getMessage())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    })
                                    .create();
                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.show();
                                }
                            });
                        }
                    }
                    tableLayout.removeAllViews();
                    for (TableRow row : list) {
                        tableLayout.addView(row);
                    }
                } else {
                    ((TextView)getView().findViewById(R.id.loadingText)).setText("No recent updates");
                }
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }
}

