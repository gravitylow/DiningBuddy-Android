package net.gravitydevelopment.cnu.modal;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Detailed information about a location, including people & crowded values.
 */
public class InfoItem implements Serializable {

    public String location;
    public int people;
    public int crowded;

    public InfoItem(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public int getPeople() {
        return people;
    }

    public CrowdedRating getCrowdedRating() {
        return CrowdedRating.values()[crowded];
    }

    @Override
    public String toString() {
        return "InfoItem{location=" + location + ", people=" + people + ", crowded = " + crowded + "}";
    }

    public enum CrowdedRating {
        NOT_CROWDED("Not crowded at all", "#2ab081"),
        SOMEWHAT_CROWDED("Somewhat crowded", "#f39c12"),
        CROWDED("Very crowded", "#d94130");

        private String mText;
        private int mColor;

        private CrowdedRating(String text, String hex) {
            this.mText = text;
            mColor = Color.parseColor(hex);
        }

        public static ArrayList<String> getFeedbackList() {
            ArrayList<String> list = new ArrayList<String>();
            for (CrowdedRating rating : values()) {
                list.add(rating.getText());
            }
            return list;
        }

        public int getColor() {
            return mColor;
        }

        public String getText() {
            return mText;
        }
    }
}
