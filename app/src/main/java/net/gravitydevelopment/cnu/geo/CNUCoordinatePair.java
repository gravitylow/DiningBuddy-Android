package net.gravitydevelopment.cnu.geo;

import java.io.Serializable;

public class CNUCoordinatePair implements Serializable {

    private double mLatitude;
    private double mLongitude;

    public CNUCoordinatePair(double latitude, double longitude) {
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
        return "CNUCoordinatePair{mLatitude=" + mLatitude + ", mLongitude = " + mLongitude + "}";
    }
}
