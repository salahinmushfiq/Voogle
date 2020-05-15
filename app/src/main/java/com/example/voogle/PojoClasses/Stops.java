package com.example.voogle.PojoClasses;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Stops {
    String name;
    double lat;
    double lng;
    ArrayList<Long> routes = new ArrayList<>();
    public Stops() {
    }

    String frontOrback;


    public Stops(String name, double lat, double lng, String frontOrback, ArrayList<Long> routes) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.routes = routes;
        this.frontOrback = frontOrback;
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

    public ArrayList<String> getRoutes() {
        ArrayList<String> newRoutes = new ArrayList<>();
        for (Long intx :
                routes) {
            newRoutes.add(String.valueOf(intx));
        }
        return newRoutes;
    }

    public ArrayList<Integer> getRoutesInt() {
        ArrayList<Integer> newRoutes = new ArrayList<>();
        for (Long intx :
                routes) {
            newRoutes.add(intx.intValue());
        }
        return newRoutes;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + name + " Lat: " + lat + " Lng: " + lng;
    }
}
