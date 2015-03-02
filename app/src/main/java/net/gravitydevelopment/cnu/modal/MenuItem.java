package net.gravitydevelopment.cnu.modal;

public class MenuItem {

    public String start;
    public String end;
    public String summary;
    public String description;

    public MenuItem(String startTime, String endTime, String summary, String description) {
        this.start = startTime;
        this.end = endTime;
        this.summary = summary;
        this.description = description;
    }

    public String getStartTime() {
        return start;
    }

    public String getEndTime() {
        return end;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }
}
