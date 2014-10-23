package net.gravitydevelopment.cnu.geo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A location on campus
 * Two locations cannot intersect, but a location can have "sub locations" which are specific
 * areas inside the given location (nested locations) which specify a more accurate place.
 */
public class CNULocation implements Serializable {

    private String name;
    private List<CNUCoordinatePair> coordinatePairs;
    private List<CNULocation> subLocations;

    public CNULocation(String name, List<CNUCoordinatePair> coordinatePairs, List<CNULocation> subLocations) {
        this.name = name;
        this.coordinatePairs = coordinatePairs;
        this.subLocations = subLocations;
    }

    public CNULocation(String name, List<CNUCoordinatePair> coordinatePairs) {
        this.name = name;
        this.coordinatePairs = coordinatePairs;
        this.subLocations = new ArrayList<CNULocation>();
    }

    public CNULocation() {
        this.coordinatePairs = new ArrayList<CNUCoordinatePair>();
        this.subLocations = new ArrayList<CNULocation>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSubLocations(List<CNULocation> subLocations) {
        this.subLocations = subLocations;
    }

    public List<CNULocation> getSubLocations() {
        return subLocations;
    }

    public boolean hasSubLocations() {
        return subLocations.size() > 0;
    }

    public void addCoordinatePair(CNUCoordinatePair coordinatePair) {
        coordinatePairs.add(coordinatePair);
    }

    public boolean isInsideLocation(double latitude, double longitude) {
        int i;
        double angle = 0;
        double point1lat;
        double point1long;
        double point2lat;
        double point2long;
        int n = coordinatePairs.size();

        for (i=0;i<n;i++) {
            CNUCoordinatePair pair1 = coordinatePairs.get(i);
            CNUCoordinatePair pair2 = coordinatePairs.get((i+1)%n);
            point1lat = pair1.getLatitude() - latitude;
            point1long = pair1.getLongitude() - longitude;
            point2lat = pair2.getLatitude() - latitude;
            point2long = pair2.getLongitude() - longitude;
            angle += angle2D(point1lat, point1long, point2lat, point2long);
        }

        if (Math.abs(angle) < Math.PI) {
            return false;
        } else {
            return true;
        }
    }

    public double angle2D(double y1, double x1, double y2, double x2) {
        double dtheta, theta1, theta2;

        theta1 = Math.atan2(y1,x1);
        theta2 = Math.atan2(y2,x2);
        dtheta = theta2 - theta1;
        while (dtheta > Math.PI) {
            dtheta -= Math.PI * 2;
        }
        while (dtheta < -Math.PI) {
            dtheta += Math.PI * 2;
        }

        return(dtheta);
    }

    @Override
    public String toString() {
        return "CNULocation{"
                + "name = " + name
                + ", coordinatePairs = " + coordinatePairs
                + ", subLocations = " + subLocations
                + "}";
    }

    private String coordinatePairsJsonValue() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if (coordinatePairs.size() > 0) {
            for (CNUCoordinatePair pair : coordinatePairs) {
                builder.append(pair.jsonValue());
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
        return builder.toString();
    }

    private String subLocationsJsonValue() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if (subLocations.size() > 0) {
            for (CNULocation sub : subLocations) {
                builder.append(sub.jsonValue());
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
        return builder.toString();
    }

    public String jsonValue() {
        return "{"
            + "\"name\" : \"" + name + "\""
            + ", \"coordinatePairs\" : " + coordinatePairsJsonValue()
            + ", \"subLocations\" : " + subLocationsJsonValue()
            + "}";
    }
}
