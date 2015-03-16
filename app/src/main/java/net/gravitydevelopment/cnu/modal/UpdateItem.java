package net.gravitydevelopment.cnu.modal;

import java.util.UUID;

/**
 * An update from a user, ready to be sent to the server.
 */
public class UpdateItem {
    public UUID id;
    public double lat;
    public double lon;
    public String location;
    public long send_time;

    public UpdateItem(UUID id, double lat, double lon, LocationItem location) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;

        if (location != null) {
            this.location = location.getName();
        }

        this.send_time = System.currentTimeMillis();
    }
}
