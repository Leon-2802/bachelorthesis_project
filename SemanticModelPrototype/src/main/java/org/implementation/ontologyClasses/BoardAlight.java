package org.implementation.ontologyClasses;

import java.time.LocalDateTime;

public class BoardAlight {
    private StopPoint stopPoint;
    private String scheduledTime;
    private String plannedBay;
    private String stopSequence;

    public BoardAlight(StopPoint stopPoint, String scheduledTime, String plannedBay, String stopSequence) {
        this.stopPoint = stopPoint;
        this.scheduledTime = scheduledTime;
        this.plannedBay = plannedBay;
        this.stopSequence = stopSequence;
    }

    public StopPoint getStopPoint() {
        return stopPoint;
    }

    public void setStopPoint(StopPoint stopPoint) {
        this.stopPoint = stopPoint;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getPlannedBay() {
        return plannedBay;
    }

    public void setPlannedBay(String plannedBay) {
        this.plannedBay = plannedBay;
    }

    public String getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(String stopSequence) {
        this.stopSequence = stopSequence;
    }
}
