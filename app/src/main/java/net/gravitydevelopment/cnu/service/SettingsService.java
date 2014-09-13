package net.gravitydevelopment.cnu.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import net.gravitydevelopment.cnu.CNU;

import java.util.UUID;

public class SettingsService {

    public static final String PREFS_NAME = "CNUPrefs";
    public static final String PREFS_KEY_WIFI_ONLY = "pref_wifi_only";
    public static final String PREFS_KEY_LOCATIONS = "pref_locations";
    public static final String PREFS_KEY_UNIQUE_ID = "pref_unique_id";

    private static SharedPreferences sSettings;
    private static UUID sUUID;
    private static NetworkInfo sWifiInfo;
    private static boolean sPrefWifiOnly;

    private BackendService mBackend;

    public SettingsService(BackendService backend) {
        mBackend = backend;
        sSettings = backend.getSharedPreferences(PREFS_NAME, 0);
        sWifiInfo = ((ConnectivityManager) backend.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // Load prefs
        sUUID = getOrCreateUniqueId();
        sPrefWifiOnly = sSettings.getBoolean(PREFS_KEY_WIFI_ONLY, false);
    }

    private UUID getOrCreateUniqueId() {
        String id = getUniqueId();
        if (id == null) {
            id = createUniqueId();
        }
        return UUID.fromString(id);
    }


    private String getUniqueId() {
        return sSettings.getString(PREFS_KEY_UNIQUE_ID, null);
    }

    private String createUniqueId() {
        UUID id = UUID.randomUUID();
        SharedPreferences.Editor editor = sSettings.edit();
        editor.putString(PREFS_KEY_UNIQUE_ID, id.toString());
        editor.apply();
        Log.d(CNU.LOG_TAG, "Saved UUID: " + id);
        return id.toString();
    }

    public static boolean isWifiConnected() {
        return sWifiInfo.isConnected();
    }

    public static UUID getUUID() {
        return sUUID;
    }

    public void cacheLocations(String json) {
        SharedPreferences.Editor editor = sSettings.edit();
        editor.putString(PREFS_KEY_LOCATIONS, json);
        editor.apply();
    }

    public String getCachedLocations() {
        return sSettings.getString(PREFS_KEY_LOCATIONS, null);
    }

    public boolean getWifiOnly() {
        return sPrefWifiOnly;
    }

    public void setWifiOnly(boolean b) {
        sPrefWifiOnly = b;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putBoolean(PREFS_KEY_WIFI_ONLY, b);
        editor.apply();
    }

    public boolean getShouldConnect() {
        if (getWifiOnly()) {
            return isWifiConnected();
        } else {
            return true;
        }
    }
}
