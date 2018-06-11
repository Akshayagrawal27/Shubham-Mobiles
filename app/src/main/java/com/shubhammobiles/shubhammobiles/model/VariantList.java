package com.shubhammobiles.shubhammobiles.model;

/**
 * Created by Akshay on 26-02-2018.
 */

public class VariantList {

    private String variantName;
    private int variantQuantity;
    private String editedBy;
    private long variantBestPrice;

    public VariantList() {
    }

    public VariantList(String variantName, int variantQuantity, String editedBy) {
        this.variantName = variantName;
        this.variantQuantity = variantQuantity;
        this.editedBy = editedBy;
    }

    public VariantList(String variantName, int variantQuantity, String editedBy, long variantBestPrice) {
        this.variantName = variantName;
        this.variantQuantity = variantQuantity;
        this.editedBy = editedBy;
        this.variantBestPrice = variantBestPrice;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public int getVariantQuantity() {
        return variantQuantity;
    }

    public void setVariantQuantity(int variantQuantity) {
        this.variantQuantity = variantQuantity;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }

    public long getVariantBestPrice() {
        return variantBestPrice;
    }

    public void setVariantBestPrice(long variantBestPrice) {
        this.variantBestPrice = variantBestPrice;
    }
}
