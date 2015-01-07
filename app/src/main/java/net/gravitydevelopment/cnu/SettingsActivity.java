package net.gravitydevelopment.cnu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;

import net.gravitydevelopment.cnu.service.BackendService;
import net.gravitydevelopment.cnu.service.SettingsService;

public class SettingsActivity extends PreferenceActivity {

    private PrefsFragment mPrefsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.GRASS);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.GRASS, false));

        mPrefsFragment = new PrefsFragment();

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, mPrefsFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cnusettings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_developer) {
            PrefsFragment.addDeveloperPrefs();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PrefsFragment extends PreferenceFragment {

        private static Context context;
        private static PreferenceScreen screen;
        private static ClipboardManager clipboard;
        private static boolean developer = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = getActivity();
            clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            addPreferencesFromResource(R.xml.preferences);

            final CheckBoxPreference wifiOnlyPreference = (CheckBoxPreference) findPreference("preference_wifi_only");
            wifiOnlyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean value = (Boolean) newValue;
                    BackendService.getSettingsService().setWifiOnly(value);
                    return true;
                }
            });

            /*final CheckBoxPreference favoriteNotificationPreference = (CheckBoxPreference) findPreference("preference_notify_favorites");
            favoriteNotificationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean value = (Boolean) newValue;
                    BackendService.getSettingsService().setNotifyFavorites(value);
                    return true;
                }
            });

            final EditTextPreference favoritesPreference = (EditTextPreference) findPreference("preference_favorites");
            favoritesPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String value = (String) newValue;
                    BackendService.getSettingsService().setFavorites(value);
                    return true;
                }
            });

            final TimePreference favoritesNotifyTimePreference = (TimePreference) findPreference("preference_favorites_notify_time");
            favoritesNotifyTimePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    long time = favoritesNotifyTimePreference.getTime();
                    BackendService.getSettingsService().setFavoritesNotificationTime(time);
                    return true;
                }
            });*/


            screen = getPreferenceScreen();
        }

        public static void addDeveloperPrefs() {
            if (!developer) {
                developer = true;
                Preference id = new Preference(context);
                id.setTitle("Unique ID");
                String uuid = SettingsService.getUUID().toString();
                id.setSummary(uuid);
                screen.addPreference(id);
                final ClipData clip = ClipData.newPlainText("CNU Unique ID", uuid);
                id.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copied to the clipboard", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
            }
        }
    }

}
