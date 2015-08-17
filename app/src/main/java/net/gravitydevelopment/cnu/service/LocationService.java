package net.gravitydevelopment.cnu.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import net.gravitydevelopment.cnu.DiningBuddy;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.fragment.LocationFeedFragment;
import net.gravitydevelopment.cnu.fragment.LocationMainFragment;
import net.gravitydevelopment.cnu.geo.Locator;
import net.gravitydevelopment.cnu.listener.LocationUpdateListener;
import net.gravitydevelopment.cnu.modal.FeedbackItem;
import net.gravitydevelopment.cnu.modal.InfoItem;
import net.gravitydevelopment.cnu.modal.LocationItem;
import net.gravitydevelopment.cnu.network.API;

import java.util.List;
import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The LocationService provides all functionality relating to locations or user updates.
 */
public class LocationService {

    private static final long MIN_UPDATE = 60 * 1000;
    private static final long INFO_UPDATE = 60 * 1000;

    private static Locator mLocator;

    private static LocationManager sLocationManager;
    private static double sLastLongitude;
    private static double sLastLatitude;
    private static LocationItem sLastLocation;
    private static List<InfoItem> sLastLocationInfo;
    private static long sLastPublishedUpdate;
    private static LocationUpdateListener sListener;

    private static boolean sHasLocation;
    private static boolean sDie;
    private static String sProvider;
    private static Criteria sCriteria;

    public LocationService(final BackendService backend) {
        sListener = new LocationUpdateListener(this);

        sLocationManager = (LocationManager) backend.getSystemService(Context.LOCATION_SERVICE);
        sCriteria = new Criteria();
        sCriteria.setPowerRequirement(Criteria.POWER_LOW);
        sProvider = sLocationManager.getBestProvider(sCriteria, true);
        Log.d(DiningBuddy.LOG_TAG, "Best provider: " + sProvider);
        if (sProvider == null || sProvider.equals("passive")) {
            showNoLocationServiceDialog();
        } else {
            sLocationManager.requestLocationUpdates(sProvider, 0, 0, sListener);
        }

        mLocator = new Locator();
        Log.d(DiningBuddy.LOG_TAG, "No cache; awaiting connection to server");
        if (SettingsService.getShouldConnect()) {
            mLocator.updateLocations();
        }

        new Thread() {
            public void run() {
                while (true) {
                    if (sDie) {
                        break;
                    }
                    Log.d(DiningBuddy.LOG_TAG, "Updating info!");
                    updateInfo();
                    try {
                        Thread.sleep(INFO_UPDATE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static void showNoLocationServiceDialog() {
        if (DiningBuddy.getContext() != null) {
            new AlertDialog.Builder(DiningBuddy.getContext())
                    .setMessage(R.string.error_no_provider)
                    .setTitle(R.string.error_title)
                    .setNegativeButton("Ignore",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                    .setPositiveButton("Fix",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent intent = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    DiningBuddy.getContext().startActivity(intent);
                                }
                            }).show();
        }
    }

    public static void updateInfo() {
        if (!Locator.isSetup()) {
            Log.d(DiningBuddy.LOG_TAG, "Can't update info because locator hasn't been setup...");
            return;
        }

        sLastLocationInfo = API.getInfo();

        if (sLastLocationInfo == null) {
            return;
        }

        if (DiningBuddy.getContext() != null) {
            DiningBuddy.getContext().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DiningBuddy.updateInfo(sLastLocationInfo);
                }
            });
        }
    }

    public static void findAndDistributeLocation(final double latitude, final double longitude) {
        if (!Locator.isSetup() || !SettingsService.getShouldConnect()) {
            return;
        }

        final LocationItem location = mLocator.getLocation(latitude, longitude);

        if (DiningBuddy.getContext() != null) {
            DiningBuddy.getContext().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DiningBuddy.updateLocation(location);
                    DiningBuddy.updateLocationView(location);
                }
            });
        }

        sLastLatitude = latitude;
        sLastLongitude = longitude;
        sLastLocation = location;
        sHasLocation = true;
    }

    public static void requestFullUpdate() {
        new Thread() {
            public void run() {
                updateInfo();
                if (sProvider == null || sProvider.equals("passive")) {
                    // Let's try again to see if it's been turned on
                    sProvider = sLocationManager.getBestProvider(sCriteria, true);
                }
                if (sProvider == null || sProvider.equals("passive")) {
                    if (DiningBuddy.getContext() != null) {
                        DiningBuddy.getContext().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LocationService.showNoLocationServiceDialog();
                            }
                        });
                    }
                } else {
                    Location loc = sLocationManager.getLastKnownLocation(sProvider);
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();
                    findAndDistributeLocation(latitude, longitude);
                }
            }
        }.start();
    }

    public static double getLastLatitude() {
        return sLastLatitude;
    }

    public static double getLastLongitude() {
        return sLastLongitude;
    }

    public static LocationItem getLastLocation() {
        return sLastLocation;
    }

    public static List<InfoItem> getLastLocationInfo() {
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

    public void updateLocation(final double latitude, final double longitude) {
        if (!Locator.isSetup() || !SettingsService.getShouldConnect()) {
            return;
        }

        LocationItem location = mLocator.getLocation(latitude, longitude);
        DiningBuddy.updateLocation(location);
        DiningBuddy.updateLocationView(location);

        if (sLastPublishedUpdate == 0 || (System.currentTimeMillis() - sLastPublishedUpdate) >= MIN_UPDATE) {
            Log.d(DiningBuddy.LOG_TAG, "Posting location " + location);
            mLocator.postLocation(latitude, longitude, location, SettingsService.getUUID());
            sLastPublishedUpdate = System.currentTimeMillis();
        }

        sLastLatitude = latitude;
        sLastLongitude = longitude;
        sLastLocation = location;
        sHasLocation = true;
    }

    public void postFeedback(final LocationFeedFragment sender, final String target, final LocationItem location, final int crowded, final int minutes, final String feedback, final UUID uuid) {
        final Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                sender.onFeedbackSubmitted();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        };
        new Thread(new Runnable() {
            public void run() {
                API.sendFeedback(new FeedbackItem(uuid, target, location, crowded, minutes, feedback), callback);
            }
        }).start();
    }
}
