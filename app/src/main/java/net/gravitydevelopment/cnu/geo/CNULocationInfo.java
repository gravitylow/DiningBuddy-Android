package net.gravitydevelopment.cnu.geo;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;

public class CNULocationInfo implements Serializable {

    public enum CrowdedRating {
        NOT_CROWDED("Not crowded at all", "#2ab081"),
        SOMEWHAT_CROWDED("Somewhat crowded", "#f39c12"),
        CROWDED("Very crowded", "#d94130");

        private String text;
        private int color;

        private CrowdedRating(String text, String hex) {
            this.text = text;
            color = Color.parseColor(hex);
        }

        public int getColor() {
            return color;
        }

        public String getText() {
            return text;
        }

        public static ArrayList<String> getFeedbackList() {
            ArrayList<String> list = new ArrayList<String>();
            for (CrowdedRating rating : values()) {
                list.add(rating.getText());
            }
            return list;
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
