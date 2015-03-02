package net.gravitydevelopment.cnu.geo;

import net.gravitydevelopment.cnu.modal.LocationItem;
import net.gravitydevelopment.cnu.modal.UpdateItem;
import net.gravitydevelopment.cnu.network.API;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Locator {

    private static List<LocationItem> sLocationsList = new ArrayList<LocationItem>();
    private static boolean sSetup = false;

    public Locator() {
        sSetup = false;
    }

    public static boolean isSetup() {
        return sSetup;
    }

    public LocationItem getLocation(double latitude, double longitude) {
        for (LocationItem location : sLocationsList) {
            if (location.isInsideLocation(latitude, longitude)) {
                return location;
            }
        }
        return null;
    }

    private void updateLocations(List<LocationItem> l) {
        sLocationsList = new ArrayList<LocationItem>();
        sLocationsList.addAll(l);
    }

    public void updateLocations() {
        new Thread(new Runnable() {
            public void run() {
                updateLocations(API.getLocations());
                sSetup = true;
            }
        }).start();
    }

    public void postLocation(final double latitude, final double longitude, final LocationItem location, final UUID uuid) {
        new Thread(new Runnable() {
            public void run() {
                API.sendUpdate(new UpdateItem(uuid, latitude, longitude, location));
            }
        }).start();
    }
}
