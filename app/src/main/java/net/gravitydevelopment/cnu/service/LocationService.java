package net.gravitydevelopment.cnu.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import net.gravitydevelopment.cnu.CNU;
import net.gravitydevelopment.cnu.CNUApi;
import net.gravitydevelopment.cnu.CNULocationListener;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocator;

import java.util.Map;
import java.util.UUID;

public class LocationService extends Service {

    private static final long MIN_UPDATE = 60 * 1000;
    private static final long PEOPLE_UPDATE = 60 * 1000;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private CNUApi mAPI = new CNUApi();
    private CNULocator mLocator;
    private SharedPreferences mSettings;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mWifiInfo;

    private static double sLastLongitude;
    private static double sLastLatitude;
    private static CNULocation sLastLocation;
    private static long sLastUpdate;
    private static long sLastPublishedUpdate;

    private static boolean sRunning;
    private static boolean sHasLocation;
    private static UUID sUUID;

    public void updateLocation(final double latitude, final double longitude) {
        if (!mLocator.isSetup()) {
            return;
        }

        CNULocation location = mLocator.getLocation(latitude, longitude);
        if (CNU.isRunning()) {
            CNU.getContext().updateLocation(latitude, longitude, location);
        }

        if (mWifiInfo.isConnected()) { // TODO toggle
            if (sLastPublishedUpdate == 0 || (System.currentTimeMillis() - sLastPublishedUpdate) >= MIN_UPDATE) {
                Log.d(CNU.LOG_TAG, "Posting location " + location);
                mLocator.postLocation(latitude, longitude, location, sUUID);
                sLastPublishedUpdate = System.currentTimeMillis();
            }
        }

        sLastLatitude = latitude;
        sLastLongitude = longitude;
        sLastLocation = location;
        sHasLocation = true;
        sLastUpdate = System.currentTimeMillis();

        new Thread() {
            public void run() {
                cacheLocations();
            }
        }.start();
    }

    public void updatePeople() {
        if (!mLocator.isSetup()) {
            return;
        }

        if (CNU.isRunning()) {
            final Map<CNULocation, Integer> map = CNUApi.getPeople();
            CNU.getContext().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CNU.getContext().updatePeople(map);
                }
            });
        }
    }

    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mLocationListener = new CNULocationListener(this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        mSettings = getSharedPreferences(CNU.PREFS_NAME, 0);
        mWifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        sUUID = getOrCreateUniqueId();

        String cache = getCachedLocations();
        if (cache != null) {
            mLocator = new CNULocator(cache);
            Log.d(CNU.LOG_TAG, "Setup from cache");
        } else {
            mLocator = new CNULocator();
            Log.d(CNU.LOG_TAG, "No cache; awaiting connection to server");
        }
        if (mWifiInfo.isConnected()) { // TODO toggle
            mLocator.updateLocations(); // TODO not needed?
        }

        new Thread() {
            public void run() {
                while (true) {
                    if (mWifiInfo.isConnected()) { // TODO toggle
                        Log.d(CNU.LOG_TAG, "Updating people!");
                        updatePeople();
                    }
                    try {
                        Thread.sleep(PEOPLE_UPDATE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        sRunning = true;
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Done", Toast.LENGTH_SHORT).show();
        cacheLocations();
        sRunning = false;
    }

    private void cacheLocations() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(CNU.PREFS_KEY_LOCATIONS, mLocator.jsonValue());
        editor.commit();
        Log.d(CNU.LOG_TAG, "Saved cache: " + mLocator.jsonValue());
    }

    private String getCachedLocations() {
        return mSettings.getString(CNU.PREFS_KEY_LOCATIONS, null);
    }

    private UUID getOrCreateUniqueId() {
        String id = getUniqueId();
        if (id == null) {
            id = createUniqueId();
        }
        return UUID.fromString(id);
    }

    private String getUniqueId() {
        return mSettings.getString(CNU.PREFS_KEY_UNIQUE_ID, null);
    }

    private String createUniqueId() {
        UUID id = UUID.randomUUID();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(CNU.PREFS_KEY_UNIQUE_ID, id.toString());
        editor.commit();
        Log.d(CNU.LOG_TAG, "Saved UUID: " + id);
        return id.toString();
    }

    public static double getLastLatitude() {
        return sLastLatitude;
    }

    public static double getLastLongitude() {
        return sLastLongitude;
    }

    public static CNULocation getLastLocation() {
        return sLastLocation;
    }

    public static boolean hasLocation() {
        return sHasLocation;
    }

    public static boolean isRunning() {
        return sRunning;
    }
}
