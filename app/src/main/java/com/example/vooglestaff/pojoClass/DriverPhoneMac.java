package com.example.vooglestaff.pojoClass;

import com.google.firebase.database.Exclude;

public class DriverPhoneMac {
    private String number;
    private String macId;
    private String licenseNo = "";
    private String name;
    private String localId;
    private String dId;
    private static final String TAG = "DriverPhoneMac";
    private boolean modifiedComparison;

    public DriverPhoneMac() {

    }

    public DriverPhoneMac(String phoneNumber, boolean modifiedComparison) {

        number = phoneNumber;
        this.modifiedComparison = modifiedComparison;
    }

    @Exclude
    private boolean fullComparison(Object target) {
        if (target instanceof DriverPhoneMac) {
            return
                    modifiedComparison ?
                            ((DriverPhoneMac) target).number.equals(number)
                            : (((DriverPhoneMac) target).licenseNo.equals(licenseNo) || ((DriverPhoneMac) target).licenseNo.equals("x293"))
                            && (((DriverPhoneMac) target).macId.equals(macId) || ((DriverPhoneMac) target).macId.equals("x293"))
                            && (((DriverPhoneMac) target).name.equals(name) || ((DriverPhoneMac) target).name.equals("x293"))
                            && ((DriverPhoneMac) target).number.equals(number);
        } else return false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DriverPhoneMac && fullComparison(o);
    }

    public DriverPhoneMac(String localId, String dId, String number, String macId, String licenseNo, String name) {
        this.localId = localId;
        this.dId = dId;
        this.number = number;
        this.macId = macId;
        this.licenseNo = licenseNo;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }
}
