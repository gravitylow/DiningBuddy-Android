package net.gravitydevelopment.cnu;

public class CNULocationMenuItem {

    private String startTime;
    private String endTime;
    private String summary;
    private String description;

    public CNULocationMenuItem(String startTime, String endTime, String summary, String description) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.summary = summary;
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }
}
