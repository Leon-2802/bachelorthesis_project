package org.implementation.ontologyClasses;

public class InterchangeLeg extends TripLeg{
    private String interchangeDuration;
    public InterchangeLeg(StopPoint origin, StopPoint destination, String interchangeDuration) {
        super(origin, destination);

        this.interchangeDuration = interchangeDuration;
    }

    public String getInterchangeDuration() {
        return interchangeDuration;
    }

    public void setInterchangeDuration(String interchangeDuration) {
        this.interchangeDuration = interchangeDuration;
    }
}
