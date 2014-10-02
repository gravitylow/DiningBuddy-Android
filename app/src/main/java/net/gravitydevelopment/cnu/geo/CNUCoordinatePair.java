package net.gravitydevelopment.cnu.geo;

public class CNUCoordinatePair {

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
                + ", \"long\" : " + longitude
                + "}";
    }
}
