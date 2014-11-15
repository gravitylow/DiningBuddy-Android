package net.gravitydevelopment.cnu.geo;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;

public class CNULocationInfo implements Serializable {

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

        public int getColor() {
            return mColor;
        }

        public String getText() {
            return mText;
        }

        public static ArrayList<String> getFeedbackList() {
            ArrayList<String> list = new ArrayList<String>();
            for (CrowdedRating rating : values()) {
                list.add(rating.getText());
            }
            return list;
        }
    }

    private final String mLocationName;
    private final int mPeopleCount;
    private CrowdedRating mCrowdedRating;

    public CNULocationInfo(String location) {
        this(location, 0, 0);
    }

    public CNULocationInfo(String location, int people, int crowded) {
        this.mLocationName = location;
        this.mPeopleCount = people;
        this.mCrowdedRating = CrowdedRating.values()[crowded];
    }

    public String getLocation() {
        return mLocationName;
    }

    public int getPeople() {
        return mPeopleCount;
    }

    public CrowdedRating getCrowdedRating() {
        return mCrowdedRating;
    }

    @Override
    public String toString() {
        return "CNULocationInfo{mLocationName=" + mLocationName + ", mPeopleCount=" + mPeopleCount + ", mCrowdedRating = " + mCrowdedRating + "}";
    }
}
