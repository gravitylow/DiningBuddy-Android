package net.gravitydevelopment.cnu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cengalabs.flatui.FlatUI;

import net.gravitydevelopment.cnu.fragment.LocationBannerFragment;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;

import java.util.List;


public class CNU extends FragmentActivity {

    public static final String LOG_TAG = "CNU";
    private static CNU sContext;
    private static boolean sRunning;
    private static CNULocationView currentLocationView;
    private static LocationBannerFragment regattasFrag;
    private static LocationBannerFragment commonsFrag;
    private static LocationBannerFragment einsteinsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.GRASS);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.GRASS, false));
        setContentView(R.layout.activity_cnudining);

        if (savedInstanceState == null) {
            regattasFrag = LocationBannerFragment.newInstance("Regattas", "Regattas", R.drawable.regattas_full, Color.GRAY, true);
            commonsFrag = LocationBannerFragment.newInstance("The Commons", "Commons", R.drawable.commons_full, Color.GRAY, true);
            einsteinsFrag = LocationBannerFragment.newInstance("Einstein's", "Einsteins", R.drawable.einsteins_full, Color.GRAY, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.regattas_container, regattasFrag)
                    .replace(R.id.commons_container, commonsFrag)
                    .replace(R.id.einsteins_container, einsteinsFrag)
                    .commit();
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
    protected void onStart() {
        super.onStart();

        sRunning = true;
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (LocationService.hasLocation()) {
            updateLocation(LocationService.getLastLatitude(), LocationService.getLastLongitude(), LocationService.getLastLocation());
            updateInfo(LocationService.getLastLocationInfo());
        }
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
            Intent startActivityIntent = new Intent(this, CNUSettings.class);
            startActivity(startActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateLocation(double latitude, double longitude, CNULocation location) {
        ((TextView) findViewById(R.id.longitude)).setText("Longitude: " + longitude);
        ((TextView) findViewById(R.id.latitude)).setText("Latitude: " + latitude);
        if (location != null) {
            ((TextView) findViewById(R.id.location)).setText("Location: " + location.getName());
        } else {
            ((TextView) findViewById(R.id.location)).setText("Location: Off Campus");
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
            if (regattasFrag != null && commonsFrag != null && einsteinsFrag != null) {
                regattasFrag.updateInfo(regattasInfo);
                commonsFrag.updateInfo(commonsInfo);
                einsteinsFrag.updateInfo(einsteinsInfo);
            }
        }
    }

    public static void updateLocationViewInfo(CNULocationInfo regattasInfo, CNULocationInfo commonsInfo, CNULocationInfo einsteinsInfo) {
        if (currentLocationView != null) {
            currentLocationView.updateInfo(regattasInfo, commonsInfo, einsteinsInfo);
        }
    }

    public static CNU getContext() {
        return sContext;
    }

    public static boolean isRunning() {
        return sRunning;
    }

    public static void setCurrentLocationView(CNULocationView view) {
        currentLocationView = view;
    }

    public static CNULocationView getCurrentLocationView() {
        return currentLocationView;
    }

    public static void updateLocationView(CNULocation location) {
        if (currentLocationView != null) {
            currentLocationView.updateLocation(location);
        }
    }
}
