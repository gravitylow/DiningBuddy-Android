package net.gravitydevelopment.cnu.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import net.gravitydevelopment.cnu.DiningBuddy;

import java.util.UUID;

public class SettingsService {

    public static final String PREFS_NAME = "CNUPrefs";
    public static final String PREFS_KEY_WIFI_ONLY = "pref_wifi_only";
    public static final String PREFS_KEY_LOCATIONS = "pref_locations";
    public static final String PREFS_KEY_UNIQUE_ID = "pref_unique_id";
    public static final String PREFS_KEY_FIRST_USER_ALERT_SHOWN = "pref_first_user_alert_shown";
    public static final String PREFS_KEY_LAST_FEEDBACK_REGATTAS = "pref_last_feedback_regattas";
    public static final String PREFS_KEY_LAST_FEEDBACK_COMMONS = "pref_last_feedback_commons";
    public static final String PREFS_KEY_LAST_FEEDBACK_EINSTEINS = "pref_last_feedback_einsteins";

    private static SharedPreferences sSettings;
    private static UUID sUUID;
    private static NetworkInfo sWifiInfo;
    private static boolean sPrefWifiOnly;
    private static long sLastFeedbackRegattas;
    private static long sLastFeedbackCommons;
    private static long sLastFeedbackEinsteins;

    public SettingsService(BackendService backend) {
        sSettings = backend.getSharedPreferences(PREFS_NAME, 0);
        sWifiInfo = ((ConnectivityManager) backend.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // Load prefs
        sUUID = getOrCreateUniqueId();
        sLastFeedbackRegattas = sSettings.getLong(PREFS_KEY_LAST_FEEDBACK_REGATTAS, -1);
        sLastFeedbackCommons = sSettings.getLong(PREFS_KEY_LAST_FEEDBACK_COMMONS, -1);
        sLastFeedbackEinsteins = sSettings.getLong(PREFS_KEY_LAST_FEEDBACK_EINSTEINS, -1);
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
        Log.d(DiningBuddy.LOG_TAG, "Saved UUID: " + id);
        return id.toString();
    }

    public static boolean isWifiConnected() {
        return sWifiInfo.isConnected();
    }

    public static UUID getUUID() {
        return sUUID;
    }

    public static void cacheLocations(String json) {
        SharedPreferences.Editor editor = sSettings.edit();
        editor.putString(PREFS_KEY_LOCATIONS, json);
        editor.apply();
    }

    public String getCachedLocations() {
        return sSettings.getString(PREFS_KEY_LOCATIONS, null);
    }

    public static boolean getWifiOnly() {
        return sPrefWifiOnly;
    }

    public void setWifiOnly(boolean b) {
        sPrefWifiOnly = b;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putBoolean(PREFS_KEY_WIFI_ONLY, b);
        editor.apply();
    }

    public static boolean getShouldConnect() {
        if (getWifiOnly()) {
            return isWifiConnected();
        } else {
            return true;
        }
    }

    public static boolean getFirstUserAlertShown(Context context) {
        return context.getSharedPreferences(PREFS_NAME, 0).getBoolean(PREFS_KEY_FIRST_USER_ALERT_SHOWN, false);
    }

    public static void setFirstUserAlertShown(Context context, boolean b) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putBoolean(PREFS_KEY_FIRST_USER_ALERT_SHOWN, b);
        editor.apply();
    }

    public long getLastFeedbackRegattas() {
        return sLastFeedbackRegattas;
    }

    public long getLastFeedbackCommons() {
        return sLastFeedbackCommons;
    }

    public long getLastFeedbackEinsteins() {
        return sLastFeedbackEinsteins;
    }

    public void setLastFeedbackRegattas(long l) {
        sLastFeedbackRegattas = l;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putLong(PREFS_KEY_LAST_FEEDBACK_REGATTAS, l);
        editor.apply();
    }

    public void setLastFeedbackCommons(long l) {
        sLastFeedbackCommons = l;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putLong(PREFS_KEY_LAST_FEEDBACK_COMMONS, l);
        editor.apply();
    }

    public void setLastFeedbackEinsteins(long l) {
        sLastFeedbackEinsteins = l;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putLong(PREFS_KEY_LAST_FEEDBACK_EINSTEINS, l);
        editor.apply();
    }
}
