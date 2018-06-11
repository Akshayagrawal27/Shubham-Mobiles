package com.shubhammobiles.shubhammobiles.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.ServerValue;
import com.shubhammobiles.shubhammobiles.util.Constants;

import java.util.HashMap;

/**
 * Created by Akshay on 02-03-2018.
 */

public class BrandList implements Parcelable {

    private String brandName;
    private int brandQuantity;
    private String owner;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;

    public BrandList() {
    }

    public BrandList(String brandName, int brandQuantity, String owner, HashMap<String, Object> timestampCreated) {
        this.brandName = brandName;
        this.brandQuantity = brandQuantity;
        this.owner = owner;
        this.timestampCreated = timestampCreated;

        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }

    /*public long getTimestampLastChangedLong() {
        return (long) timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public long getTimestampCreatedLong() {
        return (long) timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }*/

    /*public void setTimestampLastChangedToNow() {
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }*/

    protected BrandList(Parcel in) {
        brandName = in.readString();
        brandQuantity = in.readInt();
        owner = in.readString();
    }

    public static final Creator<BrandList> CREATOR = new Creator<BrandList>() {
        @Override
        public BrandList createFromParcel(Parcel in) {
            return new BrandList(in);
        }

        @Override
        public BrandList[] newArray(int size) {
            return new BrandList[size];
        }
    };

    public String getBrandName() {
        return brandName;
    }

    public int getBrandQuantity() {
        return brandQuantity;
    }

    public String getOwner() {
        return owner;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(brandName);
        parcel.writeInt(brandQuantity);
        parcel.writeString(owner);
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setBrandQuantity(int brandQuantity) {
        this.brandQuantity = brandQuantity;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setTimestampLastChanged(HashMap<String, Object> timestampLastChanged) {
        this.timestampLastChanged = timestampLastChanged;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
}
