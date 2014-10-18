package net.gravitydevelopment.cnu.service;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

import net.gravitydevelopment.cnu.CNU;
import net.gravitydevelopment.cnu.CNUApi;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;
import net.gravitydevelopment.cnu.listener.CNULocationListener;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocator;

import java.util.List;
import java.util.UUID;

public class LocationService {

    private static final long MIN_UPDATE = 60 * 1000;
    private static final long PEOPLE_UPDATE = 60 * 1000;

    private CNULocator mLocator;

    private static double sLastLongitude;
    private static double sLastLatitude;
    private static CNULocation sLastLocation;
    private static List<CNULocationInfo> sLastLocationInfo;
    private static long sLastUpdate;
    private static long sLastPublishedUpdate;
    private static CNULocationListener sListener;

    private static boolean sHasLocation;
    private static boolean sDie;

    private SettingsService mSettings;

    public LocationService(final BackendService backend) {
        mSettings = BackendService.getSettingsService();

        sListener = new CNULocationListener(this);

        ((LocationManager) backend.getSystemService(Context.LOCATION_SERVICE))
                .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, sListener);

        String cache = mSettings.getCachedLocations();
        if (cache != null) {
            mLocator = new CNULocator(cache);
            Log.d(CNU.LOG_TAG, "Setup from cache");
        } else {
            mLocator = new CNULocator();
            Log.d(CNU.LOG_TAG, "No cache; awaiting connection to server");
        }
        if (mSettings.getShouldConnect()) {
            mLocator.updateLocations();
        }

        new Thread() {
            public void run() {
                while (true) {
                    if (sDie) {
                        break;
                    }
                    updateInfo();
                    try {
                        Thread.sleep(PEOPLE_UPDATE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void updateLocation(final double latitude, final double longitude) {
        if (!CNULocator.isSetup() || !mSettings.getShouldConnect()) {
            return;
        }

        CNULocation location = mLocator.getLocation(latitude, longitude);
        Log.d(CNU.LOG_TAG, "CNU is running: " + CNU.isRunning());
        if (CNU.isRunning()) {
            CNU.getContext().updateLocation(latitude, longitude, location);
        }
        CNU.updateLocationView(location);

        if (sLastPublishedUpdate == 0 || (System.currentTimeMillis() - sLastPublishedUpdate) >= MIN_UPDATE) {
            Log.d(CNU.LOG_TAG, "Posting location " + location);
            mLocator.postLocation(latitude, longitude, location, SettingsService.getUUID());
            sLastPublishedUpdate = System.currentTimeMillis();
        }

        sLastLatitude = latitude;
        sLastLongitude = longitude;
        sLastLocation = location;
        sHasLocation = true;
        sLastUpdate = System.currentTimeMillis();

        new Thread() {
            public void run() {
                mSettings.cacheLocations(mLocator.jsonValue());
            }
        }.start();
    }

    public static void updateInfo() {
        if (!CNULocator.isSetup()) {
            return;
        }

        sLastLocationInfo = CNUApi.getInfo();

        if (sLastLocationInfo == null) {
            return;
        }

        if (CNU.getContext() != null) {
            CNU.getContext().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CNU.getContext().updateInfo(sLastLocationInfo);
                }
            });
        }
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

    public static List<CNULocationInfo> getLastLocationInfo() {
        return sLastLocationInfo;
    }

    public static boolean hasLocation() {
        return sHasLocation;
    }

    public static void die(Context context) {
        sDie = true;
        ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).removeUpdates(sListener);
    }

    public static void requestImmediateUpdate() {
        if (!sDie) {
            new Thread() {
                public void run() {
                    updateInfo();
                }
            }.start();
        }
    }

    public void postFeedback(final String target, final CNULocation location, final int crowded, final int minutes, final String feedback, final UUID uuid) {
        new Thread(new Runnable() {
            public void run() {
                CNUApi.sendFeedback(target, location, crowded, minutes, feedback, System.currentTimeMillis(), uuid);
            }
        }).start();
    }
}
