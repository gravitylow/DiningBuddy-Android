package net.gravitydevelopment.cnu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cengalabs.flatui.FlatUI;

import net.gravitydevelopment.cnu.fragment.LocationViewFragment;
import net.gravitydevelopment.cnu.geo.CNUFence;
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
    private CNUFence fence = new CNUFence();
    private LocationViewFragment regattasFrag;
    private LocationViewFragment commonsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.GRASS);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.GRASS, false));
        setContentView(R.layout.activity_cnudining);

        if (savedInstanceState == null) {
            regattasFrag = LocationViewFragment.newInstance("Regattas", "Regattas", R.drawable.regattas_full, Color.GRAY, true);
            commonsFrag = LocationViewFragment.newInstance("The Commons", "Commons", R.drawable.commons_full, Color.GRAY, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.regattas_container, regattasFrag)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.commons_container, commonsFrag)
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

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fence.getSize() == 4) {
                    ((TextView)findViewById(R.id.fence)).setText(fence.jsonValue());
                } else {
                    fence.addBound(BackendService.getLocationService().getLastLatitude(), BackendService.getLocationService().getLastLongitude());
                }
            }
        });
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
        Log.d(LOG_TAG, "CNU-updateLocation");
        ((TextView) findViewById(R.id.longitude)).setText("Longitude: " + longitude);
        ((TextView) findViewById(R.id.latitude)).setText("Latitude: " + latitude);
        if (location != null) {
            ((TextView) findViewById(R.id.location)).setText("Location: " + location.getName());
        } else {
            ((TextView) findViewById(R.id.location)).setText("Location: Off Campus");
        }
        Log.d(LOG_TAG, "Updated location, " + (currentLocationView != null));
    }

    public void updateInfo(List<CNULocationInfo> info) {
        Log.d(LOG_TAG, "CNU-updateInfo");
        CNULocationInfo regattasInfo = null;
        CNULocationInfo commonsInfo = null;
        for (CNULocationInfo location : info) {
            if (location.getLocation().equals("Regattas")) {
                regattasInfo = location;
            } else if (location.getLocation().equals("Commons")) {
                commonsInfo = location;
            }
        }
        if (regattasInfo == null) {
            regattasInfo = new CNULocationInfo("Regattas");
        }
        if (commonsInfo == null) {
            commonsInfo = new CNULocationInfo("Commons");
        }
        if (regattasFrag != null && commonsFrag != null) {
            regattasFrag.updateInfo(regattasInfo);
            commonsFrag.updateInfo(commonsInfo);
        }
        if (currentLocationView != null) {
            currentLocationView.updateInfo(regattasInfo, commonsInfo);
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
        Log.d(LOG_TAG, "CNU-updateLocationView");
        if (currentLocationView != null) {
            currentLocationView.updateLocation(location);
        }
    }

    public static void updateLocationViewInfo(List<CNULocationInfo> info) {
        CNULocationInfo regattasInfo = null;
        CNULocationInfo commonsInfo = null;
        for (CNULocationInfo location : info) {
            if (location.getLocation().equals("Regattas")) {
                regattasInfo = location;
            } else if (location.getLocation().equals("Commons")) {
                commonsInfo = location;
            }
        }
        if (regattasInfo == null) {
            regattasInfo = new CNULocationInfo("Regattas");
        }
        if (commonsInfo == null) {
            commonsInfo = new CNULocationInfo("Commons");
        }
        if (currentLocationView != null) {
            currentLocationView.updateInfo(regattasInfo, commonsInfo);
        }
    }
}
