package net.gravitydevelopment.cnu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.cengalabs.flatui.FlatUI;

import net.gravitydevelopment.cnu.fragment.LocationBannerFragment;
import net.gravitydevelopment.cnu.fragment.LocationMainFragment;
import net.gravitydevelopment.cnu.modal.InfoItem;
import net.gravitydevelopment.cnu.modal.LocationItem;

import java.io.Serializable;

/**
 * A specific location view, showing the location's banner and tabbed information.
 */
public class LocationActivity extends FragmentActivity {

    public static final String ARG_DISPLAY_NAME = "displayName";
    public static final String ARG_NAME = "name";
    public static final String ARG_INFO = "info";
    public static final String ARG_DRAWABLE = "drawable";
    public static final String ARG_INITIAL_COLOR = "initialColor";
    public static final String ARG_SHOULD_OPEN_INFO = "shouldOpenInfo";
    public static final String ARG_SHOW_BADGE = "showBadge";

    private LocationBannerFragment mBannerFragment;
    private LocationMainFragment mMainFragment;
    private String mLocationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.GRASS);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.GRASS, false));
        setContentView(R.layout.activity_location);
        if (savedInstanceState == null) {
            Bundle b = getIntent().getExtras();
            String display = b.getString(ARG_DISPLAY_NAME);
            String name = b.getString(ARG_NAME);
            int drawable = b.getInt(ARG_DRAWABLE);
            Serializable obj = b.getSerializable(ARG_INFO);
            boolean showBadge = b.getBoolean(ARG_SHOW_BADGE);

            if (obj != null) {
                mBannerFragment = LocationBannerFragment.newInstance(display, name, drawable, InfoItem.CrowdedRating.NOT_CROWDED.getColor(), false, (InfoItem) obj, showBadge);
            } else {
                mBannerFragment = LocationBannerFragment.newInstance(display, name, drawable, InfoItem.CrowdedRating.NOT_CROWDED.getColor(), false);
            }

            mMainFragment = LocationMainFragment.newInstance(name);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.location_container, mBannerFragment)
                    .add(R.id.tab_container, mMainFragment)
                    .commit();

            this.mLocationName = name;
        }

        DiningBuddy.setCurrentLocationView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DiningBuddy.setCurrentLocationView(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cnuview_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void updateLocation(LocationItem location) {
        if (mMainFragment != null) {
            mMainFragment.updateLocation(location);
        }
        if (mBannerFragment != null) {
            mBannerFragment.updateLocation(location);
        }
    }

    public void updateInfo(InfoItem regattas, InfoItem commons, InfoItem einsteins) {
        InfoItem info = null;
        switch (mLocationName) {
            case Util.REGATTAS_NAME:
                info = regattas;
                break;
            case Util.COMMONS_NAME:
                info = commons;
                break;
            case Util.EINSTEINS_NAME:
                info = einsteins;
                break;
        }
        final InfoItem finalInfo = info;
        mBannerFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBannerFragment.updateInfo(finalInfo);
            }
        });
    }

}
