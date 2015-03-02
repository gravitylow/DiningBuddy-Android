package net.gravitydevelopment.cnu.modal;

import java.util.UUID;

public class FeedbackItem {
    public UUID id;
    public String target;
    public String location;
    public int crowded;
    public int minutes;
    public long send_time;

    public FeedbackItem(UUID id, String target, LocationItem location, int crowded, int minutes) {
        this.id = id;
        this.target = target;

        if (location != null) {
            this.location = location.getName();
        }

        this.crowded = crowded;
        this.minutes = minutes;
        this.send_time = System.currentTimeMillis();
    }
}
