package net.gravitydevelopment.cnu.modal;

import android.content.pm.PackageManager;
import android.util.Log;

import net.gravitydevelopment.cnu.DiningBuddy;

/**
 * A system-wide alert defined by the server that will display to the user as the app opens.
 */
public class AlertItem {

    public String title;
    public String message;
    public String target_os;
    public String target_version;
    public long target_time_min;
    public long target_time_max;

    public AlertItem(String title, String message, String os, String version, long timeMin, long timeMax) {
        this.title = title;
        this.message = message;
        this.target_os = os;
        this.target_version = version;
        this.target_time_min = timeMin;
        this.target_time_max = timeMax;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isApplicable() {
        String thisOS = "Android";
        String thisVersion;
        try {
            thisVersion = DiningBuddy.getContext().getPackageManager()
                    .getPackageInfo(DiningBuddy.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            thisVersion = null;
        }
        long thisTime = System.currentTimeMillis();

        // Reasons to disqualify this alert:
        if (!target_os.equals("all") && !target_os.equals(thisOS)) {
            Log.i(DiningBuddy.LOG_TAG, "Alert disqualified for target: " + target_os + ", " + thisOS);
            return false;
        }
        if (!target_version.equals("all") && thisVersion != null && !target_version.equals(thisVersion)) {
            Log.i(DiningBuddy.LOG_TAG, "Alert disqualified for version: " + target_version + ", " + thisVersion);
            return false;
        }
        if ((target_time_min != 0 && target_time_min > thisTime) || (target_time_max != 0 && target_time_max < thisTime)) {
            Log.i(DiningBuddy.LOG_TAG, "Alert disqualified for time: " + thisTime);
            return false;
        }
        return true;
    }
}
