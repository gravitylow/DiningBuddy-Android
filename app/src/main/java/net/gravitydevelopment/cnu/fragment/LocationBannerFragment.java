package net.gravitydevelopment.cnu.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gravitydevelopment.cnu.DiningBuddy;
import net.gravitydevelopment.cnu.LocationActivity;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;

import java.io.Serializable;

public class LocationBannerFragment extends Fragment {

    private String mLocationName;
    private String mLocationDisplayName;
    private int mDrawable;
    private CNULocationInfo mInfo;
    private int mInitialColor;
    private boolean mShouldOpenInfo;
    private boolean mShowBadge;

    public static LocationBannerFragment newInstance(String title, String name, int drawable, int initialColor, boolean shouldOpenInfo) {
        return newInstance(title, name, drawable, initialColor, shouldOpenInfo, null, false);
    }

    public static LocationBannerFragment newInstance(String title, String name, int drawable, int initialColor, boolean shouldOpenInfo, CNULocationInfo initialInfo, boolean showBadge) {
        LocationBannerFragment fragment = new LocationBannerFragment();

        Bundle args = new Bundle();
        args.putString(LocationActivity.ARG_DISPLAY_NAME, title);
        args.putString(LocationActivity.ARG_NAME, name);
        args.putInt(LocationActivity.ARG_DRAWABLE, drawable);
        args.putInt(LocationActivity.ARG_INITIAL_COLOR, initialColor);
        args.putBoolean(LocationActivity.ARG_SHOULD_OPEN_INFO, shouldOpenInfo);
        args.putBoolean(LocationActivity.ARG_SHOW_BADGE, showBadge);
        if (initialInfo != null) {
            args.putSerializable(LocationActivity.ARG_INFO, initialInfo);
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocationName = getArguments().getString(LocationActivity.ARG_NAME);
            mLocationDisplayName = getArguments().getString(LocationActivity.ARG_DISPLAY_NAME);
            mDrawable = getArguments().getInt(LocationActivity.ARG_DRAWABLE);
            mInitialColor = getArguments().getInt(LocationActivity.ARG_INITIAL_COLOR);
            mShouldOpenInfo = getArguments().getBoolean(LocationActivity.ARG_SHOULD_OPEN_INFO);
            mShowBadge = getArguments().getBoolean(LocationActivity.ARG_SHOW_BADGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_banner, container, false);
        drawTitle(rootView, mLocationDisplayName);
        drawPicture(rootView, mInitialColor);
        drawInfo(rootView, -1);
        setBadgeHidden(rootView, !mShowBadge);
        Serializable obj = getArguments().getSerializable(LocationActivity.ARG_INFO);
        if (obj != null) {
            updateInfo(rootView, (CNULocationInfo) obj);
        }
        if (mShouldOpenInfo) {
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LocationActivity.class);
                    Bundle b = new Bundle();
                    b.putString(LocationActivity.ARG_DISPLAY_NAME, mLocationDisplayName);
                    b.putString(LocationActivity.ARG_NAME, mLocationName);
                    b.putInt(LocationActivity.ARG_DRAWABLE, mDrawable);
                    if (mInfo != null) {
                        b.putSerializable(LocationActivity.ARG_INFO, mInfo);
                    }
                    Log.d(DiningBuddy.LOG_TAG, "Fragment onClick -> set show badge" + mShowBadge);
                    b.putBoolean(LocationActivity.ARG_SHOW_BADGE, mShowBadge);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }
        return rootView;
    }

    public void updateInfo(CNULocationInfo info) {
        mInfo = info;
        updateInfo(getView(), info.getPeople(), info.getCrowdedRating());
    }

    public void updateInfo(View view, CNULocationInfo info) {
        mInfo = info;
        updateInfo(view, info.getPeople(), info.getCrowdedRating());
    }

    public void updateInfo(View view, int people, CNULocationInfo.CrowdedRating crowdedRating) {
        drawPicture(view, crowdedRating.getColor());
        setTitleColor(view, crowdedRating.getColor());
        drawInfo(view, people);
    }

    private void drawTitle(View view, String title) {
        TextView textView = ((TextView) view.findViewById(R.id.title));
        textView.setText(title);
    }

    public void setTitleColor(View view, int color) {
        TextView textView = ((TextView) view.findViewById(R.id.title));
        textView.setTextColor(color);
    }

    private void drawPicture(View view, int color) {
        ImageView image = (ImageView) view.findViewById(R.id.image);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), mDrawable);
        image.setImageBitmap(Util.getRoundedRectBitmap(bm, color));
    }

    private void drawInfo(View view, int people) {
        String loading = getString(R.string.loading_text);
        String s = people < 0 ? loading : "Currently: " + people + " people.";
        ((TextView) view.findViewById(R.id.info)).setText(s);
    }

    private void setBadgeHidden(View view, boolean hidden) {
        int visibility = hidden ? View.INVISIBLE : View.VISIBLE;
        if (view == null) return;
        view.findViewById(R.id.badge).setVisibility(visibility);
    }

    public void updateLocation(CNULocation location) {
        mShowBadge = location != null && location.getName().equals(mLocationName);
        setBadgeHidden(getView(), !mShowBadge);
    }
}
