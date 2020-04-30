package com.example.vooglestaff.pojoClass;

import java.util.ArrayList;

public class Manager {
    String email;
    String password;
    String groupName;
    String managerId;
    String groupId;
    ArrayList<String>licensePlate;
    ArrayList<String>phoneNumbers;

    public ArrayList<String> getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(ArrayList<String> licensePlate) {
        this.licensePlate = licensePlate;
    }

    int phoneNoCount,busIdCount;

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public int getPhoneNoCount() {
        return phoneNoCount;
    }

    public void setPhoneNoCount(int phoneNoCount) {
        this.phoneNoCount = phoneNoCount;
    }

    public int getBusIdCount() {
        return busIdCount;
    }

    public void setBusIdCount(int busIdCount) {
        this.busIdCount = busIdCount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }




}
