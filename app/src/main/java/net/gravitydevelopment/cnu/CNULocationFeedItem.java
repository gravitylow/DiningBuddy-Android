package net.gravitydevelopment.cnu;

public class CNULocationFeedItem {

    private String mMessage;
    private int mMinutes;
    private int mCrowded;
    private long mTime;
    private boolean mPinned;
    private String mDetail;

    public CNULocationFeedItem(String message, int minutes, int crowded, long time, boolean pinned, String detail) {
        this.mMessage = message;
        this.mMinutes = minutes;
        this.mCrowded = crowded;
        this.mTime = time;
        this.mPinned = pinned;
        this.mDetail = detail;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getMinutes() {
        return mMinutes;
    }

    public int getCrowded() {
        return mCrowded;
    }

    public long getTime() {
        return mTime;
    }

    public boolean isPinned() {
        return mPinned;
    }

    public String getDetail() {
        return mDetail;
    }
}
