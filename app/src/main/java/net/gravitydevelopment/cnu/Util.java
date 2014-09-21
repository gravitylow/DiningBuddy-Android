package net.gravitydevelopment.cnu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import net.gravitydevelopment.cnu.geo.CNULocationInfo;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.SettingsService;

public class Util {

    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int crowdedColor) {
        int width = 800;
        int height = 300;
        int colorExtra = 5;
        bitmap = Bitmap.createScaledBitmap(bitmap, width + colorExtra, height + colorExtra, true);
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(width + colorExtra, height + colorExtra, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            BitmapShader shader;
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            Paint color = new Paint();
            color.setColor(crowdedColor);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(shader);

            RectF rectColor = new RectF(0.0f, 0.0f, width + colorExtra, height + colorExtra);
            RectF rectImage = new RectF(colorExtra, colorExtra, width, height);

            canvas.drawRoundRect(rectColor, 50, 50, color);
            canvas.drawRoundRect(rectImage, 50, 50, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }

    public static int getColorForCrowdedRating(CNULocationInfo.CrowdedRating rating) {
        if (rating == CNULocationInfo.CrowdedRating.SOMEWHAT_CROWDED) {
            return Color.YELLOW;
        } else if (rating == CNULocationInfo.CrowdedRating.CROWDED) {
            return Color.RED;
        } else {
            return Color.GREEN;
        }
    }

    public static boolean externalShouldConnect(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SettingsService.PREFS_NAME, 0);
        boolean wifiOnly = prefs.getBoolean(SettingsService.PREFS_KEY_WIFI_ONLY, false);

        if (wifiOnly) {
            NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (info.isConnectedOrConnecting() && !BackendService.isRunning()) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static void startBackend(Context context) {
        Log.d(CNU.LOG_TAG, "Starting service...");
        Intent startServiceIntent = new Intent(context, BackendService.class);
        context.startService(startServiceIntent);
    }

    public static void stopBackend(Context context) {
        Log.d(CNU.LOG_TAG, "Stopping service...");
        context.stopService(new Intent(context, BackendService.class));
    }
}
