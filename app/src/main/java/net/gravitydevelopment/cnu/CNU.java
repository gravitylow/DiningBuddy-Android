package net.gravitydevelopment.cnu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.gravitydevelopment.cnu.fragment.LocationViewFragment;
import net.gravitydevelopment.cnu.geo.CNUFence;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;
import net.gravitydevelopment.cnu.geo.CNULocator;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CNU extends Activity {

    public static final String LOG_TAG = "CNU";
    private static CNU sContext;
    private static boolean sRunning;
    private CNUFence fence = new CNUFence();
    private LocationViewFragment regattasFrag;
    private LocationViewFragment commonsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnudining);

        if (savedInstanceState == null) {
            regattasFrag = LocationViewFragment.newInstance("Regattas", R.drawable.regattas_full, Color.GRAY, true);
            commonsFrag = LocationViewFragment.newInstance("The Commons", R.drawable.commons_full, Color.GRAY, true);
            getFragmentManager().beginTransaction()
                    .replace(R.id.regattas_container, regattasFrag)
                    .commit();
            getFragmentManager().beginTransaction()
                    .replace(R.id.commons_container, commonsFrag)
                    .commit();
        }

        if (!BackendService.isRunning()) {
            Log.d(LOG_TAG, "Started service");
            Intent startServiceIntent = new Intent(this, BackendService.class);
            startService(startServiceIntent);
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

    public void updateInfo(List<CNULocationInfo> info) {
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
        regattasFrag.updateInfo(regattasInfo);
        commonsFrag.updateInfo(commonsInfo);
    }

    public static CNU getContext() {
        return sContext;
    }

    public static boolean isRunning() {
        return sRunning;
    }
}
