package net.gravitydevelopment.cnu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.cengalabs.flatui.FlatUI;

import net.gravitydevelopment.cnu.fragment.LocationGraphFragment;
import net.gravitydevelopment.cnu.fragment.LocationViewFragment;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;

import java.io.Serializable;


public class CNUViewLocation extends Activity {

    public static final String ARG_TITLE = "title";
    public static final String ARG_NAME = "name";
    public static final String ARG_INFO = "info";
    public static final String ARG_DRAWABLE = "drawable";
    public static final String ARG_INITIAL_COLOR = "initialColor";
    public static final String ARG_SHOULD_OPEN_INFO = "shouldOpenInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.GRASS);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.GRASS, false));
        setContentView(R.layout.activity_cnuview_location);
        if (savedInstanceState == null) {
            Bundle b = getIntent().getExtras();
            String title = b.getString(ARG_TITLE);
            String name = b.getString(ARG_NAME);
            int drawable = b.getInt(ARG_DRAWABLE);
            Serializable obj = b.getSerializable(ARG_INFO);
            LocationViewFragment viewFragment;
            if (obj != null) {
                viewFragment = LocationViewFragment.newInstance(title, name, drawable, CNULocationInfo.CrowdedRating.NOT_CROWDED.getColor(), false, (CNULocationInfo)obj);
            } else {
                viewFragment = LocationViewFragment.newInstance(title, name, drawable, CNULocationInfo.CrowdedRating.NOT_CROWDED.getColor(), false);
            }

            LocationGraphFragment graphFragment = LocationGraphFragment.newInstance(name);

            getFragmentManager().beginTransaction()
                    .add(R.id.location_container, viewFragment)
                    .add(R.id.graph_container, graphFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cnuview_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
