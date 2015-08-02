package net.gravitydevelopment.cnu.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import net.gravitydevelopment.cnu.DiningBuddy;
import net.gravitydevelopment.cnu.R;
import net.gravitydevelopment.cnu.Util;
import net.gravitydevelopment.cnu.modal.MenuItem;
import net.gravitydevelopment.cnu.network.API;
import net.gravitydevelopment.cnu.service.SettingsService;

import java.util.List;

/**
 * This receiver recognizes wake-ups set by the user's favorite settings and parses the day's menu,
 * deciding what notifications to send to the device.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(SettingsService.PREFS_NAME, 0);
        String favorites = prefs.getString(SettingsService.PREFS_KEY_FAVORITES, null);

        if (favorites != null) {
            final String[] favoritesList = favorites.split(",");
            new Thread() {
                public void run() {
                    List<MenuItem> regattas = API.getMenu(Util.REGATTAS_NAME);
                    List<MenuItem> commons = API.getMenu(Util.COMMONS_NAME);

                    StringBuilder regattasItems = new StringBuilder();
                    StringBuilder commonsItems = new StringBuilder();

                    int count = 0;

                    for (MenuItem item : regattas) {
                        for (String string : favoritesList) {
                            if (item.getDescription().toLowerCase().contains(string.toLowerCase().trim())) {
                                regattasItems.append(string.trim())
                                        .append(" at ")
                                        .append(item.getSummary().toLowerCase())
                                        .append(", ");
                                count++;
                            }
                        }
                    }
                    for (MenuItem item : commons) {
                        for (String string : favoritesList) {
                            if (item.getDescription().toLowerCase().contains(string.toLowerCase().trim())) {
                                commonsItems.append(string.trim())
                                        .append(" at ")
                                        .append(item.getSummary().toLowerCase())
                                        .append(", ");
                                count++;
                            }
                        }
                    }

                    if (count > 0) {

                        String value = "";

                        if (regattasItems.length() > 0) {
                            regattasItems.delete(regattasItems.length() - 2, regattasItems.length());
                            value += "Regattas is serving " + regattasItems.toString() + ".";
                        }

                        if (commonsItems.length() > 0) {
                            commonsItems.delete(commonsItems.length() - 2, commonsItems.length());
                            if (value.length() > 0) {
                                value += " ";
                            }
                            value += "Commons is serving " + regattasItems.toString() + ".";
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

                        if (value.length() >= 40) {
                            NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
                            style.bigText(value);
                            String shortText = value.substring(0, 35) + "...";
                            notificationBuilder.setContentText(shortText);
                            notificationBuilder.setStyle(style);
                        }

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                        notificationManager.notify(Util.NOTIFICATION_ID_FAVORITES, notificationBuilder.build());
                    }
                }
            }.start();
        }
    }
}
