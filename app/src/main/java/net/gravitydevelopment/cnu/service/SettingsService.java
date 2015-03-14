package net.gravitydevelopment.cnu.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import net.gravitydevelopment.cnu.DiningBuddy;
import net.gravitydevelopment.cnu.receiver.AlarmReceiver;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class SettingsService {

    public static final String PREFS_NAME = "CNUPrefs";
    public static final String PREFS_KEY_WIFI_ONLY = "pref_wifi_only";
    public static final String PREFS_KEY_NOTIFY_FAVORITES = "pref_notify_favorites";
    public static final String PREFS_KEY_FAVORITES = "pref_favorites";
    public static final String PREFS_KEY_LOCATIONS = "pref_locations";
    public static final String PREFS_KEY_UNIQUE_ID = "pref_unique_id";
    public static final String PREFS_KEY_ALERTS_READ = "pref_alerts_read";
    public static final String PREFS_KEY_LAST_FEEDBACK_REGATTAS = "pref_last_feedback_regattas";
    public static final String PREFS_KEY_LAST_FEEDBACK_COMMONS = "pref_last_feedback_commons";
    public static final String PREFS_KEY_LAST_FEEDBACK_EINSTEINS = "pref_last_feedback_einsteins";
    private static SharedPreferences sSettings;
    private static UUID sUUID;
    private static NetworkInfo sWifiInfo;
    private static boolean sPrefWifiOnly;
    private static boolean sPrefNotifyFavorites;
    private static String sPrefFavorites;
    private static long sPrefFavoritesNotificationTime;
    private static long sLastFeedbackRegattas;
    private static long sLastFeedbackCommons;
    private static long sLastFeedbackEinsteins;
    private static List<String> sAlertsRead;
    private BackendService mBackendService;
    private ConnectivityManager mConnectivityManager;

    public SettingsService(BackendService backend) {
        mBackendService = backend;
        Log.d(DiningBuddy.LOG_TAG, "Set connectivity manager");
        mConnectivityManager = (ConnectivityManager) backend.getSystemService(Context.CONNECTIVITY_SERVICE);
        sSettings = backend.getSharedPreferences(PREFS_NAME, 0);
        sWifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // Load prefs
        sUUID = getOrCreateUniqueId();
        sLastFeedbackRegattas = sSettings.getLong(PREFS_KEY_LAST_FEEDBACK_REGATTAS, -1);
        sLastFeedbackCommons = sSettings.getLong(PREFS_KEY_LAST_FEEDBACK_COMMONS, -1);
        sLastFeedbackEinsteins = sSettings.getLong(PREFS_KEY_LAST_FEEDBACK_EINSTEINS, -1);
        sPrefNotifyFavorites = sSettings.getBoolean(PREFS_KEY_NOTIFY_FAVORITES, false);
        sPrefFavorites = sSettings.getString(PREFS_KEY_FAVORITES, "");

        setupNotification();
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
        return !getWifiOnly() || isWifiConnected();
    }

    public static boolean isAlertRead(Context context, String alert) {
        alert = makeSafeAlert(alert);

        return context.getSharedPreferences(PREFS_NAME, 0)
                .getStringSet(PREFS_KEY_ALERTS_READ, new HashSet<String>())
                .contains(alert);
    }

    public static void setAlertRead(Context context, String alert) {
        alert = makeSafeAlert(alert);

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Set<String> alerts = prefs.getStringSet(PREFS_KEY_ALERTS_READ, new HashSet<String>());

        Set<String> newAlerts = new HashSet<String>();
        newAlerts.addAll(alerts);
        newAlerts.add(alert);

        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(PREFS_KEY_ALERTS_READ);
        editor.commit();

        editor.putStringSet(PREFS_KEY_ALERTS_READ, newAlerts);
        editor.commit();
    }

    private static String makeSafeAlert(String alert) {
        return alert.replaceAll("\"", "").replaceAll(" ", "").replaceAll("\\.", "").replaceAll(":", "");
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

    public String getCachedLocations() {
        return sSettings.getString(PREFS_KEY_LOCATIONS, null);
    }

    public long getLastFeedbackRegattas() {
        return sLastFeedbackRegattas;
    }

    public void setLastFeedbackRegattas(long l) {
        sLastFeedbackRegattas = l;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putLong(PREFS_KEY_LAST_FEEDBACK_REGATTAS, l);
        editor.apply();
    }

    public long getLastFeedbackCommons() {
        return sLastFeedbackCommons;
    }

    public void setLastFeedbackCommons(long l) {
        sLastFeedbackCommons = l;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putLong(PREFS_KEY_LAST_FEEDBACK_COMMONS, l);
        editor.apply();
    }

    public long getLastFeedbackEinsteins() {
        return sLastFeedbackEinsteins;
    }

    public void setLastFeedbackEinsteins(long l) {
        sLastFeedbackEinsteins = l;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putLong(PREFS_KEY_LAST_FEEDBACK_EINSTEINS, l);
        editor.apply();
    }

    public boolean getNotifyFavorites() {
        return sPrefNotifyFavorites;
    }

    public void setNotifyFavorites(boolean value) {
        sPrefNotifyFavorites = value;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putBoolean(PREFS_KEY_NOTIFY_FAVORITES, value);
        editor.apply();

        setupNotification();
    }

    public String getFavorites() {
        return sPrefFavorites;
    }

    public void setFavorites(String favorites) {
        sPrefFavorites = favorites;

        SharedPreferences.Editor editor = sSettings.edit();
        editor.putString(PREFS_KEY_FAVORITES, favorites);
        editor.apply();
    }

    private void setupNotification() {
        Context context = mBackendService.getApplicationContext();
        Intent intent = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Log.d(DiningBuddy.LOG_TAG, "Alarm unscheduled");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        // Fudge to help the poor server
        calendar.set(Calendar.MINUTE, new Random().nextInt(5));
        calendar.set(Calendar.SECOND, new Random().nextInt(50));

        // Cancel existing alarms
        alarmManager.cancel(pendingIntent);
        if (sPrefNotifyFavorites) {
            // Schedule new alarm
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.d(DiningBuddy.LOG_TAG, "Alarm scheduled for " + calendar.getTimeInMillis());
        }
    }

    public ConnectivityManager getConnectivityManager() {
        Log.d(DiningBuddy.LOG_TAG, "Get connectivity manager");
        return mConnectivityManager;
    }
}
