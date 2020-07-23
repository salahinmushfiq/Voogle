package com.example.vooglestaff.pojoClass;

import java.util.ArrayList;

public class Manager {
    String email;
    String password;
    String groupName;
    String managerId;
    String groupId;

    public int getDriverPhoneNoCount() {
        return driverPhoneNoCount;
    }

    public void setDriverPhoneNoCount(int driverPhoneNoCount) {
        this.driverPhoneNoCount = driverPhoneNoCount;
    }

    ArrayList<String>licensePlate;
    ArrayList<String>driverPhoneNumbers;
    ArrayList<String>checkerPhoneNumbers;
    int driverPhoneNoCount,busIdCount;
    public ArrayList<String> getDriverPhoneNumbers() {
        return driverPhoneNumbers;
    }

    public void setDriverPhoneNumbers(ArrayList<String> driverPhoneNumbers) {
        this.driverPhoneNumbers = driverPhoneNumbers;
    }

    public ArrayList<String> getCheckerPhoneNumbers() {
        return checkerPhoneNumbers;
    }

    public void setCheckerPhoneNumbers(ArrayList<String> checkerPhoneNumbers) {
        this.checkerPhoneNumbers = checkerPhoneNumbers;
    }

    public ArrayList<String> getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(ArrayList<String> licensePlate) {
        this.licensePlate = licensePlate;
    }



    public ArrayList<String> getPhoneNumbers() {
        return driverPhoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<String> driverPhoneNumbers) {
        this.driverPhoneNumbers = driverPhoneNumbers;
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
