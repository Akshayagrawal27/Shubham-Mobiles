package com.shubhammobiles.shubhammobiles.model;

import com.google.firebase.database.ServerValue;
import com.shubhammobiles.shubhammobiles.util.Constants;

import java.util.HashMap;

/**
 * Created by Akshay on 14-03-2018.
 */

public class PriceList {

    private String shopName;
    private long variantPrice;
    private HashMap<String, Object> timeStampLastChanged;

    public PriceList() {
    }

    public PriceList(String shopName, long variantPrice) {
        this.shopName = shopName;
        this.variantPrice = variantPrice;

        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timeStampLastChanged = timestampNowObject;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public long getVariantPrice() {
        return variantPrice;
    }

    public void setVariantPrice(long variantPrice) {
        this.variantPrice = variantPrice;
    }

    public HashMap<String, Object> getTimeStampLastChanged() {
        return timeStampLastChanged;
    }

    public void setTimeStampLastChanged(HashMap<String, Object> timeStampLastChanged) {
        this.timeStampLastChanged = timeStampLastChanged;
    }
}
