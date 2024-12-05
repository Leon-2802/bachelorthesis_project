package org.implementation.ontologyClasses;

public class StopPoint {
    private String name;
    private String stopID;
    private boolean wheelchairBoarding;
    public StopPoint(String name, String stopID) {
        this.name = name;
        this.stopID = stopID;
    }
    public StopPoint(String name, String stopID, boolean wheelchairBoarding) {
        this.name = name;
        this.stopID = stopID;
        this.wheelchairBoarding = wheelchairBoarding;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStopID() {
        return stopID;
    }

    public void setStopID(String stopID) {
        this.stopID = stopID;
    }

    public boolean isWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public void setWheelchairBoarding(boolean wheelchairBoarding) {
        this.wheelchairBoarding = wheelchairBoarding;
    }
}
