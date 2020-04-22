package com.example.voogle.PojoClasses;

import androidx.annotation.NonNull;

public class Stops {
    String name;
    double lat;
    double lng;

    public Stops() {
    }

    String frontOrback;
    int routes[];

    public Stops(String name, double lat, double lng, String frontOrback, int[] routes) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.frontOrback = frontOrback;
        this.routes = routes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getFrontOrback() {
        return frontOrback;
    }

    public void setFrontOrback(String frontOrback) {
        this.frontOrback = frontOrback;
    }

    public int[] getRoutes() {
        return routes;
    }

    public void setRoutes(int[] routes) {
        this.routes = routes;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: "+name+" Lat: "+lat+" Lng: "+lng;
    }
}
