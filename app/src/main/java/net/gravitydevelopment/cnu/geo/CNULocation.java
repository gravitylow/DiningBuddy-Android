package net.gravitydevelopment.cnu.geo;

import java.util.ArrayList;
import java.util.List;

/**
 * A location on campus
 * Two locations cannot intersect, but a location can have "sub locations" which are specific
 * areas inside the given location (nested locations) which specify a more accurate place.
 */
public class CNULocation {

    private String name;
    private List<CNUFence> fences;
    private List<CNULocation> subLocations;

    public CNULocation(String name, List<CNUFence> fences, List<CNULocation> subLocations) {
        this.name = name;
        this.fences = fences;
        this.subLocations = subLocations;
    }

    public CNULocation(String name, List<CNUFence> fences) {
        this.name = name;
        this.fences = fences;
        this.subLocations = new ArrayList<CNULocation>();
    }

    public CNULocation() {
        this.fences = new ArrayList<CNUFence>();
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

    public void addFence(CNUFence fence) {
        fences.add(fence);
    }

    public boolean isInsideLocation(double latitude, double longitude) {
        for (CNUFence fence : fences) {
            if (fence.isInsideFence(latitude, longitude)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "CNULocation{"
                + "name = " + name
                + ", fences = " + fences
                + ", subLocations = " + subLocations
                + "}";
    }

    private String fencesJsonValue() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if (fences.size() > 0) {
            for (CNUFence fence : fences) {
                builder.append(fence.jsonValue());
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
            + ", \"fences\" : " + fencesJsonValue()
            + ", \"subLocations\" : " + subLocationsJsonValue()
            + "}";
    }
}
