package net.gravitydevelopment.cnu.geo;

import net.gravitydevelopment.cnu.CNUApi;
import net.gravitydevelopment.cnu.service.SettingsService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CNULocator {

    private static List<CNULocation> locations = new ArrayList<CNULocation>();
    private static boolean setup = false;

    public CNULocator() {
        setup = false;
    }

    public CNULocator(String json) {
        locations = CNUApi.locationsFromJson(json);
        setup = true;
    }

    public CNULocation getLocation(double latitude, double longitude) {
        for (CNULocation location : locations) {
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
        locations = new ArrayList<CNULocation>();
        locations.addAll(l);
        new Thread() {
            public void run() {
                SettingsService.cacheLocations(jsonValue());
            }
        }.start();
    }

    public void updateLocations() {
        new Thread(new Runnable() {
            public void run() {
                updateLocations(CNUApi.getLocations());
                setup = true;
            }
        }).start();
    }

    public static void newLocation(final CNULocation location) {
        new Thread(new Runnable() {
            public void run() {
                CNUApi.addLocation(location);
                addLocation(location);
            }
        }).start();
    }

    public void postLocation(final double latitude, final double longitude, final CNULocation location, final UUID uuid) {
        new Thread(new Runnable() {
            public void run() {
                CNUApi.updateLocation(latitude, longitude, location, System.currentTimeMillis(), uuid);
            }
        }).start();
    }

    public static List<CNULocation> getLocations() {
        return locations;
    }

    public static List<CNULocation> getAllLocations() {
        return recursiveGetLocations(new ArrayList<CNULocation>(), locations);
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
        locations.add(location);
    }

    public static boolean isSetup() {
        return setup;
    }

    public String jsonValue() {
        StringBuilder builder = new StringBuilder("[");
        if (locations.size() > 0) {
            for (CNULocation location : locations) {
                builder.append(location.jsonValue());
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
        return builder.toString();
    }
}
