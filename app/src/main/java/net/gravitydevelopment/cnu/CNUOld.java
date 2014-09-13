package net.gravitydevelopment.cnu;

import android.app.Activity;
import android.content.Intent;
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
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CNUOld extends Activity {

    private CNUFence mCurrentFence = new CNUFence();

    private static CNUOld sContext;
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
                    mCurrentFence.addBound(LocationService.getLastLatitude(), LocationService.getLastLongitude());
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
                    for (CNULocation location : CNULocator.getAllLocations()) {
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
                        CNULocator.newLocation(location);
                    }
                    mCurrentFence = new CNUFence();
                    ((EditText)findViewById(R.id.name)).setText("Name");
                    ((Button) findViewById(R.id.add_button)).setText("1/4");
                } else {
                    for (CNULocation location : CNULocator.getAllLocations()) {
                    }
                }
            }
        });
        sContext = this;
        if (!BackendService.isRunning()) {
            Intent startServiceIntent = new Intent(this, BackendService.class);
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

    public static CNUOld getContext() {
        return sContext;
    }

    public static boolean isRunning() {
        return sRunning;
    }
}
