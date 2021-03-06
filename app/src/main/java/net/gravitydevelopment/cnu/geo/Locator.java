package net.gravitydevelopment.cnu.geo;

import net.gravitydevelopment.cnu.modal.LocationItem;
import net.gravitydevelopment.cnu.modal.UpdateItem;
import net.gravitydevelopment.cnu.network.API;
import net.gravitydevelopment.cnu.service.LocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The locator service holds the list of current locations and assists in getting the applicable
 * location for a user's geo coordinates.
 */
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

    public List<LocationItem> getLocations() {
        return sLocationsList;
    }

    private void updateLocations(List<LocationItem> l) {
        sLocationsList = new ArrayList<LocationItem>();
        sLocationsList.addAll(l);
        LocationService.requestImmediateUpdate();
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
        final Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        };
        new Thread(new Runnable() {
            public void run() {
                API.sendUpdate(new UpdateItem(uuid, latitude, longitude, location), callback);
            }
        }).start();
    }
}
