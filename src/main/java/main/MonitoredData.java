package main;

import java.time.Duration;
import java.time.LocalDateTime;

public class MonitoredData {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String activityLabel;

    public MonitoredData(LocalDateTime st, LocalDateTime et, String al) {
        this.activityLabel = al;
        this.endTime = et;
        this.startTime = st;
    }

    public String getActivityLabel() {
        return activityLabel;
    }

    public String getDate() {
        return startTime.getYear() + "-" + startTime.getMonthValue() + "-" + startTime.getDayOfMonth();
    }

    public long getDuration() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MonitoredData that = (MonitoredData) o;

        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        return activityLabel != null ? activityLabel.equals(that.activityLabel) : that.activityLabel == null;

    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (activityLabel != null ? activityLabel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Activity: " + activityLabel + "\nStart time: " + startTime.toString().replace("T", " ") + "\nEnd time: " + endTime.toString().replace("T", " ");
    }
}
