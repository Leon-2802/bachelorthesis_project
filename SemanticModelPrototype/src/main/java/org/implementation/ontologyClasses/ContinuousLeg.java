package org.implementation.ontologyClasses;

public class ContinuousLeg extends TripLeg{
    private String walkDuration;
    public ContinuousLeg(StopPoint origin, StopPoint destination, String walkDuration) {
        super(origin, destination);
        this.walkDuration = walkDuration;
    }

    public String getWalkDuration() {
        return walkDuration;
    }

    public void setWalkDuration(String walkDuration) {
        this.walkDuration = walkDuration;
    }
}
