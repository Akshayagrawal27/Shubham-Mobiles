package com.shubhammobiles.shubhammobiles.model;

import com.shubhammobiles.shubhammobiles.util.Constants;

/**
 * Created by Akshay on 04-03-2018.
 */

public class OrderList {

    private String bookingDate;
    private String billNumber;
    private String customerName;
    private String customerPhoneNumber;
    private String customerAddress;
    private String modelName;
    private String variant;
    private int quantity;
    private long amount;
    private long advancePaid;
    private long dueAmount;
    private String deliveryDate;
    private String status;

    public OrderList() {
    }

    public OrderList(String bookingDate, String billNumber, String customerName, String customerPhoneNumber, String customerAddress,
                     String modelName, String variant, int quantity, long amount, long advancePaid, String deliveryDate) {

        this.billNumber = billNumber;
        this.customerName = customerName.toLowerCase();
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerAddress = customerAddress.toLowerCase();
        this.modelName = modelName.toLowerCase();
        this.variant = variant;
        this.quantity = quantity;
        this.amount = amount;
        this.advancePaid = advancePaid;
        this.deliveryDate = deliveryDate;
        this.dueAmount = amount - advancePaid;
        this.bookingDate = bookingDate;
        this.status = Constants.ORDER_STATUS_PENDING;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getAdvancePaid() {
        return advancePaid;
    }

    public void setAdvancePaid(long advancePaid) {
        this.advancePaid = advancePaid;
    }

    public long getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(long dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
