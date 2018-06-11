package com.shubhammobiles.shubhammobiles.model;

import com.google.firebase.database.ServerValue;
import com.shubhammobiles.shubhammobiles.util.Constants;

import java.util.HashMap;

/**
 * Created by Akshay on 09-03-2018.
 */

public class AccountList {

    private String accountName;
    private String accountAddress;
    private String contactNumber;
    private String owner;
    private HashMap<String, Object> timeStampLastChanged;

    public AccountList() {
    }

    public AccountList(String accountName, String accountAddress, String contactNumber, String owner) {
        this.accountName = accountName;
        this.accountAddress = accountAddress;
        this.contactNumber = contactNumber;
        this.owner = owner;

        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timeStampLastChanged = timestampNowObject;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public HashMap<String, Object> getTimeStampLastChanged() {
        return timeStampLastChanged;
    }

    public void setTimeStampLastChanged(HashMap<String, Object> timeStampLastChanged) {
        this.timeStampLastChanged = timeStampLastChanged;
    }
}
