package net.gravitydevelopment.cnu;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.gravitydevelopment.cnu.geo.CNUFence;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocator;
import net.gravitydevelopment.cnu.service.LocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CNU extends Activity {

    public static final String LOG_TAG = "CNU";
    public static final String PREFS_NAME = "CNUPrefs";
    public static final String PREFS_KEY_LOCATIONS = "locations";
    public static final String PREFS_KEY_UNIQUE_ID = "unique-id";

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private CNUFence mCurrentFence = new CNUFence();
    private CNUApi mAPI = new CNUApi();
    private CNULocator mLocator;

    private static CNU sContext;
    private static boolean sRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnu);

        //setupLocationListener();
        //setupLocations();

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentFence.getSize() < 4) {
                    Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    mCurrentFence.addBound(location.getLatitude(), location.getLongitude());
                    if (mCurrentFence.getSize() <= 4) {
                        ((Button) findViewById(R.id.add_button)).setText(mCurrentFence.getSize() + "/4");
                    }
                }
            }
        });
        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentFence.getSize() == 4) {
                    String name = ((EditText)findViewById(R.id.name)).getText().toString();
                    boolean found = false;
                    for (CNULocation location : mLocator.getLocations()) {
                        if (location.getName().equalsIgnoreCase(name)) {
                            //location.addFence(mCurrentFence);
                            //found = true;
                            //Log.d(LOG_TAG, "ADD: " + location.toString());
                            //TODO
                        }
                    }
                    if (!found) {
                        List<CNUFence> list = new ArrayList<CNUFence>();
                        list.add(mCurrentFence);
                        CNULocation location = new CNULocation(name, list);
                        mLocator.newLocation(location);
                        Log.d(LOG_TAG, "ADD: " + location.toString());
                    }
                    mCurrentFence = new CNUFence();
                    ((EditText)findViewById(R.id.name)).setText("Name");
                    ((Button) findViewById(R.id.add_button)).setText("1/4");
                } else {
                    for (CNULocation location : mLocator.getLocations()) {
                        Log.d(LOG_TAG, "LIST: " + location.toString());
                    }
                }
            }
        });
        sContext = this;
        if (!LocationService.isRunning()) {
            Log.d(LOG_TAG, "Started location service");
            Intent startServiceIntent = new Intent(this, LocationService.class);
            startService(startServiceIntent);
        } else if (LocationService.hasLocation()) {
            updateLocation(
                    LocationService.getLastLatitude(),
                    LocationService.getLastLongitude(),
                    LocationService.getLastLocation()
            );
        }
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

    private void setupLocationListener() {
        //mLocationManager = (LocationManager) this.getSystemService(sContext.LOCATION_SERVICE);
        //mLocationListener = new CNULocationListener(this);
        //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
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
            ((TextView) findViewById(R.id.location)).setText("Location: unknown");
        }
    }

    public void updatePeople(Map<CNULocation, Integer> map) {
        StringBuilder builder = new StringBuilder("Locations:\n");
        for (CNULocation location : map.keySet()) {
            builder.append(location.getName());
            builder.append(":");
            builder.append(map.get(location));
            builder.append("\n");
        }
        ((TextView) findViewById(R.id.locations)).setText(builder.toString());
    }

    public static CNU getContext() {
        return sContext;
    }

    public static boolean isRunning() {
        return sRunning;
    }
}
