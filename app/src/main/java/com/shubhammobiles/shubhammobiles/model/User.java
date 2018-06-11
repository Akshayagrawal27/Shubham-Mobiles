package com.shubhammobiles.shubhammobiles.model;

import java.util.HashMap;

/**
 * Created by Akshay on 28-02-2018.
 */

public class User {

    private String shopName;
    private String ownerName;
    private String phoneNumber;
    private HashMap<String, Object> timestampJoined;

    public User() {
    }

    public User(String shopName, String ownerName, String phoneNumber, HashMap<String, Object> timestampJoined) {
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.timestampJoined = timestampJoined;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public void setTimestampJoined(HashMap<String, Object> timestampJoined) {
        this.timestampJoined = timestampJoined;
    }
}
