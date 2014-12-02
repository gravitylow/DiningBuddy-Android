package net.gravitydevelopment.cnu.receiver;

import net.gravitydevelopment.cnu.API;
import net.gravitydevelopment.cnu.CNULocationMenuItem;
import net.gravitydevelopment.cnu.DiningBuddy;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.service.SettingsService;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(DiningBuddy.LOG_TAG, "Broadcast!");
        SharedPreferences prefs = context.getSharedPreferences(SettingsService.PREFS_NAME, 0);
        String favorites = prefs.getString(SettingsService.PREFS_KEY_FAVORITES, null);

        if (favorites != null) {
            final String[] favoritesList = favorites.split(",");
            new Thread() {
                public void run() {
                    List<CNULocationMenuItem> regattas = API.getMenu(Util.REGATTAS_NAME);
                    List<CNULocationMenuItem> commons = API.getMenu(Util.COMMONS_NAME);

                    StringBuilder regattasItems = new StringBuilder();
                    StringBuilder commonsItems = new StringBuilder();

                    int count = 0;

                    for (CNULocationMenuItem item : regattas) {
                        for (String string : favoritesList) {
                            if (item.getDescription().toLowerCase().contains(string.toLowerCase())) {
                                regattasItems.append(string + " at " + item.getSummary().toLowerCase() + ", ");
                                count++;
                            }
                        }
                    }
                    for (CNULocationMenuItem item : commons) {
                        for (String string : favoritesList) {
                            if (item.getDescription().toLowerCase().contains(string.toLowerCase())) {
                                commonsItems.append(string + " at " + item.getSummary().toLowerCase()  + ", ");
                                count++;
                            }
                        }
                    }

                    if (count > 0) {

                        String value = "";

                        if (regattasItems.length() > 0) {
                            regattasItems.delete(regattasItems.length() - 2, regattasItems.length());
                            value += "Regattas is serving: " + regattasItems.toString() + ".";
                        }

                        if (commonsItems.length() > 0) {
                            commonsItems.delete(commonsItems.length() - 2, commonsItems.length());
                            if (value.length() > 0) {
                                value += " ";
                            }
                            value += "Commons is serving: " + regattasItems.toString() + ".";
                        }

                        Intent viewIntent = new Intent(context, DiningBuddy.class);
                        PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, 0);

                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("Your favorites are here!")
                                        .setContentText(value)
                                        .setAutoCancel(true)
                                        .setContentIntent(viewPendingIntent);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                        notificationManager.notify(Util.NOTIFICATION_ID_FAVORITES, notificationBuilder.build());
                    }
                }
            }.start();
        }
    }
}
