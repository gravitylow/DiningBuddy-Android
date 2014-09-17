package net.gravitydevelopment.cnu.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import net.gravitydevelopment.cnu.service.LocationService;

public class CNULocationListener implements LocationListener {

    private LocationService locationService;

    public CNULocationListener(LocationService locationService) {
        this.locationService = locationService;
    }

    public void onLocationChanged(Location location) {
        locationService.updateLocation(location.getLatitude(), location.getLongitude());
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onProviderEnabled(String provider) {}

    public void onProviderDisabled(String provider) {}

}
