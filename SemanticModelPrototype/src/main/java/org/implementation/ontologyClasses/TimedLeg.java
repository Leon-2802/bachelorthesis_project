package org.implementation.ontologyClasses;

import java.util.List;

public class TimedLeg  extends TripLeg{
    private BoardAlight board;
    private BoardAlight alight;
    private String line;
    private String transitMode;
    private boolean realtimeData;
    private List<String> information;

    public TimedLeg(StopPoint origin, StopPoint destination, BoardAlight board, BoardAlight alight, String line, String transitMode, boolean realtimeData, List<String> information) {
        super(origin, destination);
        this.board = board;
        this.alight = alight;
        this.line = line;
        this.transitMode = transitMode;
        this.realtimeData = realtimeData;
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

    public boolean isRealtimeData() {
        return realtimeData;
    }

    public void setRealtimeData(boolean realtimeData) {
        this.realtimeData = realtimeData;
    }

    public List<String> getInformation() {
        return information;
    }

    public void setInformation(List<String> information) {
        this.information = information;
    }
}
