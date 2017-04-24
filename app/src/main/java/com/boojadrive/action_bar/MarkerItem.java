package com.boojadrive.action_bar;

/**
 * Created by Administrator on 2017-02-18.
 */

public class MarkerItem {
    double lat;
    double lon;
    String direction;

    public MarkerItem(double lat, double lon, String direction) {
        this.lat = lat;
        this.lon = lon;
        this.direction = direction;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getdirection() {
        return direction;
    }

}
