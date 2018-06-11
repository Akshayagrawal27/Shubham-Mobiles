package com.shubhammobiles.shubhammobiles.model;

import java.util.HashMap;

/**
 * Defines the data structure for both Active and Archived ShoppingList objects.
 */

public class BrandModelList {
    private String brandModelName;
    private int brandModelQty;

    public BrandModelList() {
    }

    public BrandModelList(String brandModelName, int brandModelQty) {
        this.brandModelName = brandModelName;
        this.brandModelQty = brandModelQty;
    }

    public String getBrandModelName() {
        return brandModelName;
    }

    public void setBrandModelName(String brandModelName) {
        this.brandModelName = brandModelName;
    }

    public int getBrandModelQty() {
        return brandModelQty;
    }

    public void setBrandModelQty(int brandModelQty) {
        this.brandModelQty = brandModelQty;
    }
}

