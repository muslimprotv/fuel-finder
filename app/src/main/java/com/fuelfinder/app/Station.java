package com.fuelfinder.app;

public class Station {
    public final String placeId;
    public final String name;
    public final double lat;
    public final double lng;
    public final float distanceMeters;

    public Station(String placeId, String name, double lat, double lng, float distanceMeters) {
        this.placeId = placeId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.distanceMeters = distanceMeters;
    }
}
