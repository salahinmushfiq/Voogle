package com.example.voogle.PojoClasses;

import java.util.ArrayList;

public class RouteNew {
    ArrayList<StopsNew> stopsNew = new ArrayList<>();

    public RouteNew() {
    }

    public RouteNew(ArrayList<StopsNew> stopsNew) {
        this.stopsNew = stopsNew;
    }

    public ArrayList<StopsNew> getStopsNew() {
        return stopsNew;
    }

    public void setStopsNew(ArrayList<StopsNew> stopsNew) {
        this.stopsNew = stopsNew;
    }
}
