package net.gravitydevelopment.cnu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class BackendService extends Service {

    private static BackendService sBackendService;
    private static LocationService sLocationService;
    private static SettingsService sSettingsService;
    private static boolean sRunning;

    @Override
    public void onCreate() {
        sSettingsService = new SettingsService(this);
        sLocationService = new LocationService(this);
        sBackendService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        sRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LocationService.die(this);
        //Toast.makeText(this, "Service Done", Toast.LENGTH_SHORT).show();
        sRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
}
