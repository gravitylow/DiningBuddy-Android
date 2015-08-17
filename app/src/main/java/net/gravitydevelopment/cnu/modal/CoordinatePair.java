package net.gravitydevelopment.cnu.modal;

import java.io.Serializable;

/**
 * A latitude, longitude combination.
 */
public class CoordinatePair implements Serializable {

    private double mLatitude;
    private double mLongitude;

    public CoordinatePair(double latitude, double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    @Override
    public String toString() {
        return "CoordinatePair{mLatitude=" + mLatitude + ", mLongitude = " + mLongitude + "}";
    }
}
