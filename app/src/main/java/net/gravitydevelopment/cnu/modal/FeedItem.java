package net.gravitydevelopment.cnu.modal;

public class FeedItem {

    public String feedback;
    public int minutes;
    public int crowded;
    public long time;
    public boolean pinned;
    public String detail;

    public FeedItem(String feedback, int minutes, int crowded, long time, boolean pinned, String detail) {
        this.feedback = feedback;
        this.minutes = minutes;
        this.crowded = crowded;
        this.time = time;
        this.pinned = pinned;
        this.detail = detail;
    }

    public String getFeedback() {
        return feedback;
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

    public String getDetail() {
        return detail;
    }
}
