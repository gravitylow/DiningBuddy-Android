package net.gravitydevelopment.cnu.geo;

public class CNULocationInfo {

    private final String location;
    private final int people;

    public CNULocationInfo(String location, int people) {
        this.location = location;
        this.people = people;
    }

    public String getLocation() {
        return location;
    }

    public int getPeople() {
        return people;
    }
}
