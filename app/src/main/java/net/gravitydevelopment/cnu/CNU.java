package net.gravitydevelopment.cnu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.gravitydevelopment.cnu.geo.CNUFence;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocator;
import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.LocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CNU extends Activity {

    public static final String LOG_TAG = "CNU";
    private static CNU sContext;
    private static boolean sRunning;
    private CNUFence fence = new CNUFence();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnudining);

        ImageView regattas = (ImageView) findViewById(R.id.regattasImage);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.regattas_full);
        regattas.setImageBitmap(getRoundedRectBitmap(bm));

        ImageView commons = (ImageView) findViewById(R.id.commonsImage);
        bm = BitmapFactory.decodeResource(getResources(), R.drawable.commons_full);

        commons.setImageBitmap(getRoundedRectBitmap(bm));

        if (!BackendService.isRunning()) {
            Log.d(LOG_TAG, "Started service");
            Intent startServiceIntent = new Intent(this, BackendService.class);
            startService(startServiceIntent);
        } else if (LocationService.hasLocation()) {
            updateLocation(
                    LocationService.getLastLatitude(),
                    LocationService.getLastLongitude(),
                    LocationService.getLastLocation()
            );
        }
        sContext = this;

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fence.getSize() == 4) {
                    List<CNUFence> list = new ArrayList<CNUFence>();
                    list.add(fence);
                    CNULocator.addLocation(new CNULocation("Regattas", list));
                } else {
                    fence.addBound(BackendService.getLocationService().getLastLatitude(), BackendService.getLocationService().getLastLongitude());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        sRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        sRunning = false;
    }

    private Bitmap getRoundedRectBitmap(Bitmap bitmap) {
        int width = 800;
        int height = 300;
        int colorExtra = 4;
        bitmap = Bitmap.createScaledBitmap(bitmap, width + colorExtra, height + colorExtra, true);
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(width + colorExtra, height + colorExtra, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            BitmapShader shader;
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            Paint color = new Paint();
            color.setColor(Color.GREEN);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cnu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startActivityIntent = new Intent(this, CNUSettings.class);
            startActivity(startActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateLocation(double latitude, double longitude, CNULocation location) {
        ((TextView) findViewById(R.id.longitude)).setText("Longitude: " + longitude);
        ((TextView) findViewById(R.id.latitude)).setText("Latitude: " + latitude);
        if (location != null) {
            ((TextView) findViewById(R.id.location)).setText("Location: " + location.getName());
        } else {
            ((TextView) findViewById(R.id.location)).setText("Location: unknown");
        }
    }

    public void updatePeople(Map<CNULocation, Integer> map) {
        Log.d(LOG_TAG, "Updated people: " + map.size());
        int regattas = 0;
        int commons = 0;
        for (CNULocation location : map.keySet()) {
            if (location.getName().equals("Regattas")) {
                regattas = map.get(location);
            } else if (location.getName().equals("Commons")) {
                commons = map.get(location);
            }
        }
        ((TextView) findViewById(R.id.regattasInfo)).setText("Currently: " + regattas + " people.");
        ((TextView) findViewById(R.id.commonsInfo)).setText("Currently: " + commons + " people.");
    }

    public static CNU getContext() {
        return sContext;
    }

    public static boolean isRunning() {
        return sRunning;
    }
}
