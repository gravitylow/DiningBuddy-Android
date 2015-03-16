package net.gravitydevelopment.cnu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The Backend holds all the necessary services and is started when the device obtains a valid
 * network connection.
 */
public class BackendService extends Service {

    private static BackendService sBackendService;
    private static LocationService sLocationService;
    private static SettingsService sSettingsService;
    private static boolean sRunning;
    private static boolean sAlertsShown;

    public static void setAlertsShown() {
        sAlertsShown = true;
    }

    public static BackendService getBackendService() {
        return sBackendService;
    }

    public static LocationService getLocationService() {
        return sLocationService;
    }

    public static SettingsService getSettingsService() {
        return sSettingsService;
    }

    public static boolean isRunning() {
        return sRunning;
    }

    public static boolean alertsShown() {
        return sAlertsShown;
    }

    @Override
    public void onCreate() {
        sSettingsService = new SettingsService(this);
        sLocationService = new LocationService(this);
        sBackendService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LocationService.die(this);
        sRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
