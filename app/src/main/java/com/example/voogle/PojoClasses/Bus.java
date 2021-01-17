package com.example.voogle.PojoClasses;

public class Bus {
    long groupId;
    String groupName;
    int route_no;
    double ratings;

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getRoute_no() {
        return route_no;
    }

    public void setRoute_no(int route_no) {
        this.route_no = route_no;
    }
}
