package net.gravitydevelopment.cnu.geo;

import net.gravitydevelopment.cnu.API;
import net.gravitydevelopment.cnu.service.SettingsService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CNULocator {

    private static List<CNULocation> sLocationsList = new ArrayList<CNULocation>();
    private static boolean sSetup = false;
    private String jsonValue;

    public CNULocator() {
        sSetup = false;
    }

    public CNULocator(String json) {
        jsonValue = json;
        sLocationsList = API.locationsFromJson(json);
        sSetup = true;
    }

    public static boolean isSetup() {
        return sSetup;
    }

    public CNULocation getLocation(double latitude, double longitude) {
        for (CNULocation location : sLocationsList) {
            if (location.isInsideLocation(latitude, longitude)) {
                return location;
            }
        }
        return null;
    }

    private void updateLocations(List<CNULocation> l) {
        sLocationsList = new ArrayList<CNULocation>();
        sLocationsList.addAll(l);
        new Thread() {
            public void run() {
                SettingsService.cacheLocations(jsonValue());
            }
        }.start();
    }

    public void updateLocations() {
        new Thread(new Runnable() {
            public void run() {
                String json = API.getLocations();
                if (json != null) {
                    jsonValue = json;
                    updateLocations(API.locationsFromJson(json));
                    sSetup = true;
                }
            }
        }).start();
    }

    public void postLocation(final double latitude, final double longitude, final CNULocation location, final UUID uuid) {
        new Thread(new Runnable() {
            public void run() {
                API.updateLocation(latitude, longitude, location, System.currentTimeMillis(), uuid);
            }
        }).start();
    }

    public List<CNULocation> getLocations() {
        return sLocationsList;
    }

    public String jsonValue() {
        return jsonValue;
    }
}
