package net.gravitydevelopment.cnu.geo;

import java.io.Serializable;

public class CNUCoordinatePair implements Serializable {

    private double latitude;
    private double longitude;

    public CNUCoordinatePair(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String jsonValue() {
        return "{"
                + "\"lat\" : " + latitude
                + ", \"lon\" : " + longitude
                + "}";
    }
}
