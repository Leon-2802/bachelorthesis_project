package org.implementation.ontologyClasses;

public class TripLeg {

    protected StopPoint origin;
    protected StopPoint destination;
    public TripLeg(StopPoint origin, StopPoint destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public StopPoint getOrigin() {
        return origin;
    }

    public StopPoint getDestination() {
        return destination;
    }
}
