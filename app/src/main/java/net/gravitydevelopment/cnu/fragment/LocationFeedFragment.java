package net.gravitydevelopment.cnu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import net.gravitydevelopment.cnu.LocationActivity;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.modal.FeedItem;
import net.gravitydevelopment.cnu.network.API;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.SettingsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays the feed and the feedback box for a location.
 */
public class LocationFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String mLocationName;
    private SettingsService mSettingsService;
    private TableLayout mTableLayout;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView mBackgroundText;
    private EditText mFeedbackBox;
    private Button mFeedbackButton;

    public LocationFeedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocationName = getArguments().getString(LocationActivity.ARG_NAME);
        }
        mSettingsService = BackendService.getSettingsService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_feed, container, false);
        mTableLayout = (TableLayout) rootView.findViewById(R.id.table);
        new Thread(new Runnable() {
            public void run() {
                updateFeed(API.getFeed(mLocationName));
            }
        }).start();

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.view_feed_refresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(
                R.color.orange_primary,
                R.color.sea_primary,
                R.color.sky_primary,
                R.color.grass_primary);

        mBackgroundText = (TextView) rootView.findViewById(R.id.background_text);

        mFeedbackBox = (EditText) rootView.findViewById(R.id.feedback_box);
        mFeedbackButton = (Button) rootView.findViewById(R.id.feedback_button);

        if (!shouldShowFeedback()) {
            mFeedbackBox.setAlpha(0.0F);
            mFeedbackButton.setAlpha(0.0F);
        }
        mFeedbackBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFeedbackBox.clearFocus();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    DialogFragment fragment = LocationFeedbackFragment.newInstance(mLocationName);
                    fragment.show(ft, "dialog");
                }
            }
        });

        return rootView;
    }

    public void onFeedbackSubmitted() {
        onRefresh();
    }

    public void checkFeedback() {
        animateFeedback(shouldShowFeedback());
    }

    private boolean shouldShowFeedback() {
        long lastUpdate =
            mLocationName.equals(Util.REGATTAS_NAME) ? mSettingsService.getLastFeedbackRegattas()
                : mLocationName.equals(Util.COMMONS_NAME) ? mSettingsService.getLastFeedbackCommons()
                : mSettingsService.getLastFeedbackEinsteins();
        return (System.currentTimeMillis() - lastUpdate) >= Util.MIN_FEEDBACK_INTERVAL;
    }

    private void animateFeedback(boolean visible) {
        LinearLayout layout = (LinearLayout) mRefreshLayout.findViewById(R.id.feedback_layout);
        float v = visible ? 1.0F : 0.0F;
        int d = visible ? 0 : layout.getHeight();
        if (v == layout.getAlpha()) {
            return;
        }
        layout.animate()
                .translationY(d)
                .alpha(v)
                .setDuration(500);
    }

    @Override
    public void onRefresh() {
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(true);
        }
        new Thread(new Runnable() {
            public void run() {
                updateFeed(API.getFeed(mLocationName));
            }
        }).start();
    }

    public void updateFeed(final List<FeedItem> items) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkFeedback();
                mTableLayout.removeAllViews();
                if (items.size() > 0) {
                    mBackgroundText.setVisibility(View.INVISIBLE);
                    List<TableRow> list = new ArrayList<TableRow>();
                    int i = 0;
                    for (FeedItem item : items) {
                        String message = item.getFeedback();
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
                                    .setMessage(item.getFeedback())
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
                    for (TableRow row : list) {
                        mTableLayout.addView(row);
                    }
                } else {
                    mBackgroundText.setText(getString(R.string.empty_feed_text));
                    mBackgroundText.setVisibility(View.VISIBLE);
                }
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }
}

