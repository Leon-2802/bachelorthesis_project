package org.implementation.ontologyClasses;

import java.util.List;

public class TimedLeg  extends TripLeg{
    private BoardAlight board;
    private BoardAlight alight;
    private String line;
    private String transitMode;
    private List<String> information;
    private boolean wheelchairAccessible;
    private boolean bikesAllowed;

    public TimedLeg(StopPoint origin, StopPoint destination, BoardAlight board, BoardAlight alight, String line, String transitMode, List<String> information) {
        super(origin, destination);
        this.board = board;
        this.alight = alight;
        this.line = line;
        this.transitMode = transitMode;
        this.information = information;
    }

    public BoardAlight getBoard() {
        return board;
    }

    public void setBoard(BoardAlight board) {
        this.board = board;
    }

    public BoardAlight getAlight() {
        return alight;
    }

    public void setAlight(BoardAlight alight) {
        this.alight = alight;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getTransitMode() {
        return transitMode;
    }

    public void setTransitMode(String transitMode) {
        this.transitMode = transitMode;
    }

    public List<String> getInformation() {
        return information;
    }

    public void setInformation(List<String> information) {
        this.information = information;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public boolean isBikesAllowed() {
        return bikesAllowed;
    }

    public void setBikesAllowed(boolean bikesAllowed) {
        this.bikesAllowed = bikesAllowed;
    }
}
