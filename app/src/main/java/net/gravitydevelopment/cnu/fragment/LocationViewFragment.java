package net.gravitydevelopment.cnu.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gravitydevelopment.cnu.CNU;
import net.gravitydevelopment.cnu.CNUViewLocation;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;

import java.io.Serializable;

public class LocationViewFragment extends Fragment {

    private String mTitle;
    private String mName;
    private int mDrawable;
    private CNULocationInfo mInfo;
    private int mInitialColor;
    private boolean mShouldOpenInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(CNUViewLocation.ARG_TITLE);
            mName = getArguments().getString(CNUViewLocation.ARG_NAME);
            mDrawable = getArguments().getInt(CNUViewLocation.ARG_DRAWABLE);
            mInitialColor = getArguments().getInt(CNUViewLocation.ARG_INITIAL_COLOR);
            mShouldOpenInfo = getArguments().getBoolean(CNUViewLocation.ARG_SHOULD_OPEN_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_view, container, false);
        drawTitle(rootView, mTitle);
        drawPicture(rootView, mInitialColor);
        drawInfo(rootView, -1);
        Serializable obj = getArguments().getSerializable(CNUViewLocation.ARG_INFO);
        if (obj != null) {
            updateInfo(rootView, (CNULocationInfo) obj);
        }
        if(mShouldOpenInfo) {
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CNUViewLocation.class);
                    Bundle b = new Bundle();
                    b.putString(CNUViewLocation.ARG_TITLE, mTitle);
                    b.putString(CNUViewLocation.ARG_NAME, mName);
                    b.putInt(CNUViewLocation.ARG_DRAWABLE, mDrawable);
                    if (mInfo != null) {
                        b.putSerializable(CNUViewLocation.ARG_INFO, mInfo);
                    }
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
        drawPicture(view, Util.getColorForCrowdedRating(crowdedRating));
        drawInfo(view, people);
    }

    private void drawTitle(View view, String title) {
        ((TextView) view.findViewById(R.id.title)).setText(title);
    }

    private void drawPicture(View view, int color) {
        ImageView image = (ImageView) view.findViewById(R.id.image);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), mDrawable);
        image.setImageBitmap(Util.getRoundedRectBitmap(bm, color));
    }

    private void drawInfo(View view, int people) {
        String s = people < 0 ? "Loading..." : "Currently: " + people + " people.";
        ((TextView) view.findViewById(R.id.info)).setText(s);
    }

    public static LocationViewFragment newInstance(String title, String name, int drawable, int initialColor, boolean shouldOpenInfo) {
        return newInstance(title, name, drawable, initialColor, shouldOpenInfo, null);
    }

    public static LocationViewFragment newInstance(String title, String name, int drawable, int initialColor, boolean shouldOpenInfo, CNULocationInfo initialInfo) {
        LocationViewFragment fragment = new LocationViewFragment();

        Bundle args = new Bundle();
        args.putString(CNUViewLocation.ARG_TITLE, title);
        args.putString(CNUViewLocation.ARG_NAME, name);
        args.putInt(CNUViewLocation.ARG_DRAWABLE, drawable);
        args.putInt(CNUViewLocation.ARG_INITIAL_COLOR, initialColor);
        args.putBoolean(CNUViewLocation.ARG_SHOULD_OPEN_INFO, shouldOpenInfo);
        if (initialInfo != null) {
            Log.d(CNU.LOG_TAG, "Created new frag: " + initialInfo);
            args.putSerializable(CNUViewLocation.ARG_INFO, initialInfo);
        }
        fragment.setArguments(args);

        return fragment;
    }
}
