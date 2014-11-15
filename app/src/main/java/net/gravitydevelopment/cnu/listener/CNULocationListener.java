package net.gravitydevelopment.cnu.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import net.gravitydevelopment.cnu.service.LocationService;

public class CNULocationListener implements LocationListener {

    private LocationService mLocationService;

    public CNULocationListener(LocationService locationService) {
        this.mLocationService = locationService;
    }

    public void onLocationChanged(Location location) {
        mLocationService.updateLocation(location.getLatitude(), location.getLongitude());
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onProviderEnabled(String provider) {}

    public void onProviderDisabled(String provider) {}
}
