package net.gravitydevelopment.cnu;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import net.gravitydevelopment.cnu.fragment.LocationViewFragment;
import net.gravitydevelopment.cnu.geo.CNULocation;
import net.gravitydevelopment.cnu.geo.CNULocationInfo;

import java.io.Serializable;


public class CNUViewLocation extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnuview_location);
        Bundle b = getIntent().getExtras();
        String title = b.getString("title");
        int drawable = b.getInt("drawable");
        Serializable obj = b.getSerializable("info");
        LocationViewFragment frag;
        if (obj != null) {
            frag = LocationViewFragment.newInstance(title, drawable, Color.GREEN, false, (CNULocationInfo)obj);
        } else {
            frag = LocationViewFragment.newInstance(title, drawable, Color.GREEN, false);
        }
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.location_container, frag)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cnuview_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
