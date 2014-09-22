package net.gravitydevelopment.cnu.geo;

import android.graphics.Color;

import java.io.Serializable;

public class CNULocationInfo implements Serializable {

    public enum CrowdedRating {
        NOT_CROWDED("#2ab081"),
        SOMEWHAT_CROWDED("#f39c12"),
        CROWDED("#d94130");

        private int color;

        private CrowdedRating(String hex) {
            color = Color.parseColor(hex);
        }

        public int getColor() {
            return color;
        }
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
