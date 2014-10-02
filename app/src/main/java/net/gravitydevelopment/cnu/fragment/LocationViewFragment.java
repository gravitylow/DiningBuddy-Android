package net.gravitydevelopment.cnu.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gravitydevelopment.cnu.CNULocationView;
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
            mTitle = getArguments().getString(CNULocationView.ARG_TITLE);
            mName = getArguments().getString(CNULocationView.ARG_NAME);
            mDrawable = getArguments().getInt(CNULocationView.ARG_DRAWABLE);
            mInitialColor = getArguments().getInt(CNULocationView.ARG_INITIAL_COLOR);
            mShouldOpenInfo = getArguments().getBoolean(CNULocationView.ARG_SHOULD_OPEN_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_view, container, false);
        drawTitle(rootView, mTitle);
        drawPicture(rootView, mInitialColor);
        drawInfo(rootView, -1);
        Serializable obj = getArguments().getSerializable(CNULocationView.ARG_INFO);
        if (obj != null) {
            updateInfo(rootView, (CNULocationInfo) obj);
        }
        if(mShouldOpenInfo) {
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CNULocationView.class);
                    Bundle b = new Bundle();
                    b.putString(CNULocationView.ARG_TITLE, mTitle);
                    b.putString(CNULocationView.ARG_NAME, mName);
                    b.putInt(CNULocationView.ARG_DRAWABLE, mDrawable);
                    if (mInfo != null) {
                        b.putSerializable(CNULocationView.ARG_INFO, mInfo);
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
        String s = people < 0 ? "Loading…" : "Currently: " + people + " people.";
        ((TextView) view.findViewById(R.id.info)).setText(s);
    }

    public static LocationViewFragment newInstance(String title, String name, int drawable, int initialColor, boolean shouldOpenInfo) {
        return newInstance(title, name, drawable, initialColor, shouldOpenInfo, null);
    }

    public static LocationViewFragment newInstance(String title, String name, int drawable, int initialColor, boolean shouldOpenInfo, CNULocationInfo initialInfo) {
        LocationViewFragment fragment = new LocationViewFragment();

        Bundle args = new Bundle();
        args.putString(CNULocationView.ARG_TITLE, title);
        args.putString(CNULocationView.ARG_NAME, name);
        args.putInt(CNULocationView.ARG_DRAWABLE, drawable);
        args.putInt(CNULocationView.ARG_INITIAL_COLOR, initialColor);
        args.putBoolean(CNULocationView.ARG_SHOULD_OPEN_INFO, shouldOpenInfo);
        if (initialInfo != null) {
            args.putSerializable(CNULocationView.ARG_INFO, initialInfo);
        }
        fragment.setArguments(args);

        return fragment;
    }
}
