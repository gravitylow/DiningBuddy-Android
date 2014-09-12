package net.gravitydevelopment.cnu.geo;

public class CNUFence {
    private int size;
    private double effectiveMinLat;
    private double effectiveMaxLat;
    private double effectiveMinLong;
    private double effectiveMaxLong;

    public CNUFence() {

    }

    public CNUFence(double minLat, double maxLat, double minLong, double maxLong) {
        this.effectiveMinLat = minLat;
        this.effectiveMaxLat = maxLat;
        this.effectiveMinLong = minLong;
        this.effectiveMaxLong = maxLong;
        size = 4;
    }

    /**
     * Add a pair of coordinates to the boundary of this fence.
     *
     * @param latitude latitude of the boundary
     * @param longitude longitude of the boundary
     * @return true if pair is added, false if there are already 4 boundaries to this fence.
     */
    public boolean addBound(double latitude, double longitude) {
        if (getSize() == 4) {
            return false;
        } else if (getSize() == 0) {
            effectiveMinLat = latitude;
            effectiveMaxLat = latitude;
            effectiveMinLong = longitude;
            effectiveMaxLong = longitude;
        } else {
            if (latitude < effectiveMinLat) effectiveMinLat = latitude;
            if (latitude > effectiveMaxLat) effectiveMaxLat = latitude;
            if (longitude < effectiveMinLong) effectiveMinLong = longitude;
            if (longitude > effectiveMaxLong) effectiveMaxLong = longitude;
        }
        size++;
        return true;
    }

    /**
     * Determine if a set of coordinates is inside this fence.
     *
     * @param latitude latitude to test
     * @param longitude longitude to test
     * @return true if latitude and longitude are within given bounds
     */
    public boolean isInsideFence(double latitude, double longitude) {
        if (getSize() != 4) {
            return false;
        }
        return latitude >= effectiveMinLat
                && latitude <= effectiveMaxLat
                && longitude >= effectiveMinLong
                && longitude <= effectiveMaxLong;
    }

    public int getSize() {
        return size;
    }

    public double getEffectiveMinLat() {
        return effectiveMinLat;
    }

    public double getEffectiveMaxLat() {
        return effectiveMaxLat;
    }

    public double getEffectiveMinLong() {
        return effectiveMinLong;
    }

    public double getEffectiveMaxLong() {
        return effectiveMaxLong;
    }

    @Override
    public String toString() {
        return "CNUFence{"
                + "size=" + size
                + ", minLat = " + effectiveMinLat
                + ", maxLat = " + effectiveMaxLat
                + ", minLong = " + effectiveMinLong
                + ", maxLong = " + effectiveMaxLong
                + "}";
    }

    public String jsonValue() {
        return "{"
                + "\"minLat\" : " + effectiveMinLat
                + ", \"maxLat\" : " + effectiveMaxLat
                + ", \"minLong\" : " + effectiveMinLong
                + ", \"maxLong\" : " + effectiveMaxLong
                + "}";
    }
}
