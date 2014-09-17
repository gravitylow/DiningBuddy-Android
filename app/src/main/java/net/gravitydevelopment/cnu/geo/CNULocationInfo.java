package net.gravitydevelopment.cnu.geo;

import java.io.Serializable;

public class CNULocationInfo implements Serializable {

    public enum CrowdedRating {
        NOT_CROWDED,
        SOMEWHAT_CROWDED,
        CROWDED;
    }

    private final String location;
    private final int people;
    private CrowdedRating crowdedRating;

    public CNULocationInfo(String location) {
        this(location, 0, 0);
    }

    public CNULocationInfo(String location, int people, int crowded) {
        this.location = location;
        this.people = people;
        this.crowdedRating = CrowdedRating.values()[crowded];
    }

    public String getLocation() {
        return location;
    }

    public int getPeople() {
        return people;
    }

    public CrowdedRating getCrowdedRating() {
        return crowdedRating;
    }

    @Override
    public String toString() {
        return "CNULocationInfo{location=" + location + ", people=" + people + ", crowdedRating = " + crowdedRating + "}";
    }
}
