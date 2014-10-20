package net.gravitydevelopment.cnu;

public class CNULocationFeedItem {

    private String message;
    private int minutes;
    private int crowded;
    private long time;
    private boolean pinned;

    public CNULocationFeedItem(String message, int minutes, int crowded, long time, boolean pinned) {
        this.message = message;
        this.minutes = minutes;
        this.crowded = crowded;
        this.time = time;
        this.pinned = pinned;
    }

    public String getMessage() {
        return message;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getCrowded() {
        return crowded;
    }

    public long getTime() {
        return time;
    }

    public boolean isPinned() {
        return pinned;
    }
}
