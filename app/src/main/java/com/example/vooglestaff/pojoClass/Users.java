package com.example.vooglestaff.pojoClass;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Keep
public class Users {
    private String address;
    private String contact;
    private String name;
    private String uid;
    private String mailAddress;

    private ArrayList<Integer> paymentIds = new ArrayList<>();


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Users() {

    }

    public Users(String Uid, String contact, String name, String address, String mailAddress) {

        this.uid = Uid;
        this.contact = contact;
        this.name = name;
        this.address = address;
        this.mailAddress = mailAddress;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }









    @NonNull
    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uid", getUid());
            jsonObject.put("name", getName());
            jsonObject.put("address", getAddress());
            jsonObject.put("contact", getContact());
            jsonObject.put("email", getMailAddress());
//            jsonObject.put("payment", getPayment().toString());
            return jsonObject.toString(4);
        } catch (JSONException X) {
            X.printStackTrace();
            return "X";
        }
    }

    public ArrayList<Integer> getPaymentIds() {
        return paymentIds;
    }

    public void setPaymentIds(ArrayList<Integer> paymentIds) {
        this.paymentIds = paymentIds;
    }
}
