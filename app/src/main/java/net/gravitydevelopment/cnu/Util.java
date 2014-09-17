package net.gravitydevelopment.cnu;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import net.gravitydevelopment.cnu.geo.CNULocationInfo;

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
}
