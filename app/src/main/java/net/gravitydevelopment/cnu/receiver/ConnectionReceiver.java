package net.gravitydevelopment.cnu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.SettingsService;

/**
 * This receiver recognizes changes in the device's connection and halts services if necessary.
 */
public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(SettingsService.PREFS_NAME, 0);
        boolean wifiOnly = prefs.getBoolean(SettingsService.PREFS_KEY_WIFI_ONLY, false);

        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiOnly) {
            if (info.isConnected()) {
                if (!BackendService.isRunning()) {
                    Util.startBackend(context);
                }
            } else {
                if (BackendService.isRunning()) {
                    Util.stopBackend(context);
                }
            }
        } else {
            if (!BackendService.isRunning()) {
                Util.startBackend(context);
            }
        }
    }
}
