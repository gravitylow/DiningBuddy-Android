package net.gravitydevelopment.cnu.geo;

import net.gravitydevelopment.cnu.API;
import net.gravitydevelopment.cnu.service.SettingsService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CNULocator {

    private static List<CNULocation> sLocationsList = new ArrayList<CNULocation>();
    private static boolean sSetup = false;

    public CNULocator() {
        sSetup = false;
    }

    public CNULocator(String json) {
        sLocationsList = API.locationsFromJson(json);
        sSetup = true;
    }

    public CNULocation getLocation(double latitude, double longitude) {
        for (CNULocation location : sLocationsList) {
            CNULocation applicable = getApplicableLocation(location, latitude, longitude);
            if (applicable != null) {
                return applicable;
            }
        }
        return null;
    }

    private CNULocation getApplicableLocation(CNULocation base, double latitude, double longitude) {
        if (!base.hasSubLocations()) {
            if (base.isInsideLocation(latitude, longitude)) {
                return base;
            } else {
                return null;
            }
        }
        for (CNULocation sub : base.getSubLocations()) {
            CNULocation loc = getApplicableLocation(sub, latitude, longitude);
            if (loc != null) {
                return loc;
            }
        }
        return base.isInsideLocation(latitude, longitude) ? base : null;
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
                updateLocations(API.getLocations());
                sSetup = true;
            }
        }).start();
    }

    public static void newLocation(final CNULocation location) {
        new Thread(new Runnable() {
            public void run() {
                API.addLocation(location);
                addLocation(location);
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

    public static List<CNULocation> getLocations() {
        return sLocationsList;
    }

    public static List<CNULocation> getAllLocations() {
        return recursiveGetLocations(new ArrayList<CNULocation>(), sLocationsList);
    }

    private static List<CNULocation> recursiveGetLocations(List<CNULocation> build, List<CNULocation> locs) {
        for (CNULocation location : locs) {
            build.add(location);
            if (location.hasSubLocations()) {
                recursiveGetLocations(build, location.getSubLocations());
            }
        }
        return build;
    }

    public static void addLocation(CNULocation location) {
        sLocationsList.add(location);
    }

    public static boolean isSetup() {
        return sSetup;
    }

    public String jsonValue() {
        StringBuilder builder = new StringBuilder("[");
        if (sLocationsList.size() > 0) {
            for (CNULocation location : sLocationsList) {
                builder.append(location.jsonValue());
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
        return builder.toString();
    }
}
