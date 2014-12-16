package net.gravitydevelopment.cnu.modals;

import android.content.pm.PackageManager;
import android.util.Log;

import net.gravitydevelopment.cnu.DiningBuddy;

public class AlertItem {

    private String mTitle;
    private String mMessage;
    private String mTargetOS;
    private String mTargetVersion;
    private long mTargetTimeMin;
    private long mTargetTimeMax;

    public AlertItem(String title, String message, String os, String version, long timeMin, long timeMax) {
        this.mTitle = title;
        this.mMessage = message;
        this.mTargetOS = os;
        this.mTargetVersion = version;
        this.mTargetTimeMin = timeMin;
        this.mTargetTimeMax = timeMax;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
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
        if (!mTargetOS.equals("all") && !mTargetOS.equals(thisOS)) {
            Log.i(DiningBuddy.LOG_TAG, "Alert disqualified for target: " + mTargetOS + ", " + thisOS);
            return false;
        }
        if (!mTargetVersion.equals("all") && thisVersion != null && !mTargetVersion.equals(thisVersion)) {
            Log.i(DiningBuddy.LOG_TAG, "Alert disqualified for version: " + mTargetVersion + ", " + thisVersion);
            return false;
        }
        if ((mTargetTimeMin != 0 && mTargetTimeMin > thisTime) || (mTargetTimeMax != 0 && mTargetTimeMax < thisTime)) {
            Log.i(DiningBuddy.LOG_TAG, "Alert disqualified for time: " + thisTime);
            return false;
        }
        return true;
    }
}
