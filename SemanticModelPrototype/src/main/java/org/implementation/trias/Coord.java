package org.implementation.trias;

public class Coord {
    private float lon, lat;

    public Coord(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }

    public String lonToString() {
        return String.format("%.5f", getLon());
    }
    public String latToString() {
        return String.format("%.5f", getLat());
    }
}
