package com.example.voogle.PojoClasses;

import java.util.ArrayList;

public class RouteNew {
    ArrayList<StopNew> stopNew = new ArrayList<>();

    public RouteNew() {
    }

    public RouteNew(ArrayList<StopNew> stopNew) {
        this.stopNew = stopNew;
    }

    public ArrayList<StopNew> getStopNew() {
        return stopNew;
    }

    public void setStopNew(ArrayList<StopNew> stopNew) {
        this.stopNew = stopNew;
    }
}
