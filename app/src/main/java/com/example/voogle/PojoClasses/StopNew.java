package com.example.voogle.PojoClasses;

public class StopNew {
    Double lat,lng;
    String name;
    Integer route,up,down;

    public StopNew() {
    }

    public StopNew(Double lat, Double lng, String name, Integer route, Integer up, Integer down) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.route = route;
        this.up = up;
        this.down = down;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoute() {
        return route;
    }

    public void setRoute(Integer route) {
        this.route = route;
    }

    public Integer getUp() {
        return up;
    }

    public void setUp(Integer up) {
        this.up = up;
    }

    public Integer getDown() {
        return down;
    }

    public void setDown(Integer down) {
        this.down = down;
    }
}
