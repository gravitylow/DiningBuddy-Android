package net.gravitydevelopment.cnu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.cengalabs.flatui.FlatUI;

import net.gravitydevelopment.cnu.fragment.LocationBannerFragment;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;
import net.gravitydevelopment.cnu.modals.AlertItem;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;
import net.gravitydevelopment.cnu.service.SettingsService;

import java.util.List;


public class DiningBuddy extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String LOG_TAG = "CNU";

    private static final String BUNDLE_LAST_LAT = "lastLatitude";
    private static final String BUNDLE_LAST_LON = "lastLongitude";
    private static final String BUNDLE_LAST_LOCATION = "lastLocation";

    private static DiningBuddy sContext;
    private static boolean sRunning;
    private static LocationActivity mCurrentLocationView;
    private static CNULocation sLastLocation;
    private static LocationBannerFragment sRegattasFrag;
    private static LocationBannerFragment sCommonsFrag;
    private static LocationBannerFragment sEinsteinsFrag;
    private static SwipeRefreshLayout sRefreshLayout;

    public static void updateLocation(double latitude, double longitude, CNULocation location) {
        sLastLocation = location;
        if (sRegattasFrag != null) {
            sRegattasFrag.updateLocation(location);
        }
        if (sCommonsFrag != null) {
            sCommonsFrag.updateLocation(location);
        }
        if (sEinsteinsFrag != null) {
            sEinsteinsFrag.updateLocation(location);
        }
    }

    public static void updateInfo(List<CNULocationInfo> info) {
        CNULocationInfo regattasInfo = null;
        CNULocationInfo commonsInfo = null;
        CNULocationInfo einsteinsInfo = null;
        for (CNULocationInfo location : info) {
            if (location.getLocation().equals(Util.REGATTAS_NAME)) {
                regattasInfo = location;
            } else if (location.getLocation().equals(Util.COMMONS_NAME)) {
                commonsInfo = location;
            } else if (location.getLocation().equals(Util.EINSTEINS_NAME)) {
                einsteinsInfo = location;
            }
        }
        if (regattasInfo == null) {
            regattasInfo = new CNULocationInfo(Util.REGATTAS_NAME);
        }
        if (commonsInfo == null) {
            commonsInfo = new CNULocationInfo(Util.COMMONS_NAME);
        }
        if (einsteinsInfo == null) {
            einsteinsInfo = new CNULocationInfo(Util.EINSTEINS_NAME);
        }
        updateLocationViewInfo(regattasInfo, commonsInfo, einsteinsInfo);
        if (isRunning()) {
            if (sRegattasFrag != null && sCommonsFrag != null && sEinsteinsFrag != null) {
                sRegattasFrag.updateInfo(regattasInfo);
                sCommonsFrag.updateInfo(commonsInfo);
                sEinsteinsFrag.updateInfo(einsteinsInfo);
            }
            if (sRefreshLayout.isRefreshing()) {
                sRefreshLayout.setRefreshing(false);
            }
        }
    }

    public static void updateLocationViewInfo(CNULocationInfo regattasInfo, CNULocationInfo commonsInfo, CNULocationInfo einsteinsInfo) {
        if (mCurrentLocationView != null) {
            mCurrentLocationView.updateInfo(regattasInfo, commonsInfo, einsteinsInfo);
        }
    }

    public static DiningBuddy getContext() {
        return sContext;
    }

    public static boolean isRunning() {
        return sRunning;
    }

    public static LocationActivity getCurrentLocationView() {
        return mCurrentLocationView;
    }

    public static void setCurrentLocationView(LocationActivity view) {
        mCurrentLocationView = view;
    }

    public static void updateLocationView(CNULocation location) {
        if (mCurrentLocationView != null) {
            mCurrentLocationView.updateLocation(location);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.GRASS);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.GRASS, false));
        setContentView(R.layout.activity_main);

        Log.w(LOG_TAG, "savedInstanceState: " + savedInstanceState);

        sRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.view_refresh);
        sRefreshLayout.setOnRefreshListener(this);
        sRefreshLayout.setColorSchemeResources(
                R.color.orange_primary,
                R.color.sea_primary,
                R.color.sky_primary,
                R.color.grass_primary);

        String regattasTitle = getString(R.string.regattas_title);
        String commonsTitle = getString(R.string.commons_title);
        String einsteinsTitle = getString(R.string.einsteins_title);
        sRegattasFrag = LocationBannerFragment.newInstance(regattasTitle, "Regattas", R.drawable.regattas_full, Color.GRAY, true);
        sCommonsFrag = LocationBannerFragment.newInstance(commonsTitle, "Commons", R.drawable.commons_full, Color.GRAY, true);
        sEinsteinsFrag = LocationBannerFragment.newInstance(einsteinsTitle, "Einsteins", R.drawable.einsteins_full, Color.GRAY, true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.regattas_container, sRegattasFrag)
                .replace(R.id.commons_container, sCommonsFrag)
                .replace(R.id.einsteins_container, sEinsteinsFrag)
                .commit();

        if (savedInstanceState != null) {
            double lat = savedInstanceState.getDouble(BUNDLE_LAST_LAT);
            double lon = savedInstanceState.getDouble(BUNDLE_LAST_LON);
            CNULocation location = (CNULocation) savedInstanceState.getSerializable(BUNDLE_LAST_LOCATION);
            updateLocation(lat, lon, location);
        }

        if (!BackendService.isRunning() && Util.externalShouldConnect(this)) {
            Util.startBackend(this);
        } else if (LocationService.hasLocation()) {
            updateLocation(
                    LocationService.getLastLatitude(),
                    LocationService.getLastLongitude(),
                    LocationService.getLastLocation()
            );
        }
        sContext = this;
    }

    @Override
    public void onRefresh() {
        LocationService.requestFullUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        sRunning = true;

        if (!BackendService.alertsShown()) {
            BackendService.setAlertsShown();
            // Check for any alerts
            new Thread() {
                public void run() {
                    final List<AlertItem> alerts = API.getAlerts();
                    DiningBuddy.getContext().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (alerts.size() > 0) {
                                for (final AlertItem item : alerts) {
                                    if (SettingsService.isAlertRead(DiningBuddy.this, item.getMessage())) {
                                        continue;
                                    }
                                    SettingsService.setAlertRead(DiningBuddy.this, item.getMessage());
                                    new AlertDialog.Builder(DiningBuddy.this)
                                            .setMessage(item.getMessage())
                                            .setTitle(item.getTitle())
                                            .setPositiveButton("Ok",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int id) {
                                                        }
                                                    }).show();
                                }

                            }
                        }
                    });
                }
            }.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        sRunning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationService.requestImmediateUpdate();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.w(LOG_TAG, "onSaveInstanceState: " + savedInstanceState);
        savedInstanceState.putDouble(BUNDLE_LAST_LAT, LocationService.getLastLatitude());
        savedInstanceState.putDouble(BUNDLE_LAST_LON, LocationService.getLastLongitude());
        savedInstanceState.putSerializable(BUNDLE_LAST_LOCATION, sLastLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.w(LOG_TAG, "onRestoreInstanceState: " + savedInstanceState);
        double lat = savedInstanceState.getDouble(BUNDLE_LAST_LAT);
        double lon = savedInstanceState.getDouble(BUNDLE_LAST_LON);
        CNULocation location = (CNULocation) savedInstanceState.getSerializable(BUNDLE_LAST_LOCATION);
        updateLocation(lat, lon, location);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cnu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(startActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
