package net.gravitydevelopment.cnu.modal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A location on campus.
 * Two locations cannot intersect, but a location can have "sub locations" which are specific
 * areas inside the given location (nested locations) which specify a more accurate place.
 */
public class LocationItem implements Serializable {

    public String mName;
    public List<CoordinatePair> mCoordinatePairList;
    public int priority = 0;

    public LocationItem(String name, List<CoordinatePair> coordinatePairs, int priority) {
        this.mName = name;
        this.mCoordinatePairList = coordinatePairs;
        this.priority = priority;
    }

    public LocationItem() {
        this.mCoordinatePairList = new ArrayList<CoordinatePair>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isInsideLocation(double latitude, double longitude) {
        int i;
        double angle = 0;
        double point1lat;
        double point1long;
        double point2lat;
        double point2long;
        int n = mCoordinatePairList.size();

        for (i = 0; i < n; i++) {
            CoordinatePair pair1 = mCoordinatePairList.get(i);
            CoordinatePair pair2 = mCoordinatePairList.get((i + 1) % n);
            point1lat = pair1.getLatitude() - latitude;
            point1long = pair1.getLongitude() - longitude;
            point2lat = pair2.getLatitude() - latitude;
            point2long = pair2.getLongitude() - longitude;
            angle += angle2D(point1lat, point1long, point2lat, point2long);
        }

        return Math.abs(angle) >= Math.PI;
    }

    public double angle2D(double y1, double x1, double y2, double x2) {
        double dtheta, theta1, theta2;

        theta1 = Math.atan2(y1, x1);
        theta2 = Math.atan2(y2, x2);
        dtheta = theta2 - theta1;
        while (dtheta > Math.PI) {
            dtheta -= Math.PI * 2;
        }
        while (dtheta < -Math.PI) {
            dtheta += Math.PI * 2;
        }

        return (dtheta);
    }

    @Override
    public String toString() {
        return "LocationItem{"
                + "mName = " + mName
                + ", mCoordinatePairList = " + mCoordinatePairList
                + "}";
    }
}
