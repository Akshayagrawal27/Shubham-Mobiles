package com.shubhammobiles.shubhammobiles.order;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.BaseActivity;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.OrderList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

import java.util.Calendar;
import java.util.regex.Pattern;

public class AddOrder extends BaseActivity {

    private EditText etBookingDate, etBillNumber, etCustomerName, etCustomerAddress, etCustomerPhoneNumber, etModelName, etVariant;
    private EditText etQuantity, etTotalAmount, etAdvanceAmount, etDeliveryDate;
    private Button btplaceOrder;

    private String mBookingDate, mBillNumber, mCustomerName, mCustomerAddress, mCustomerPhoneNumber, mModelName, mVariant;
    private String mDeliveryDate;
    private int mQuantity = 0;
    private long mTotalAmount = 0, mAdvanceAmount = 0;

    private DatePickerDialog datePickerDialog;

    private DatabaseReference orderListReference;

    String action, orderKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        Intent intent = this.getIntent();
        action = intent.getAction();

        orderListReference = FirebaseUtil.getOrderListReference();

        etBookingDate = (EditText) findViewById(R.id.et_order_booking_date);
        etBillNumber = (EditText) findViewById(R.id.et_order_bill_number);
        etCustomerName = (EditText) findViewById(R.id.et_order_customer_name);
        etCustomerAddress = (EditText) findViewById(R.id.et_order_customer_address);
        etCustomerPhoneNumber = (EditText) findViewById(R.id.et_order_customer_phone_number);
        etModelName = (EditText) findViewById(R.id.et_order_model_name);
        etVariant = (EditText) findViewById(R.id.et_order_variant);
        etQuantity = (EditText) findViewById(R.id.et_order_quantity);
        etTotalAmount = (EditText) findViewById(R.id.et_order_amount);
        etAdvanceAmount = (EditText) findViewById(R.id.et_order_advance_amount);
        etDeliveryDate = (EditText) findViewById(R.id.et_order_delivery_date);

        btplaceOrder = (Button) findViewById(R.id.bt_order_confirm);

        if (action.equals(Constants.ADD_NEW_ORDER))
            etBookingDate.setText(Utils.getCurrentDate());
        else{
            orderKey = intent.getStringExtra(Constants.KEY_ORDER);
            orderListReference.child(orderKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    OrderList orderList = dataSnapshot.getValue(OrderList.class);
                    etBookingDate.setText(Utils.getDateToShowFromFirebase(orderList.getBookingDate()));
                    etBillNumber.setText(orderList.getBillNumber());
                    etCustomerName.setText(Utils.getNameToShow(orderList.getCustomerName()));
                    etCustomerAddress.setText(Utils.getNameToShow(orderList.getCustomerAddress()));
                    etCustomerPhoneNumber.setText(orderList.getCustomerPhoneNumber().substring(3));
                    etModelName.setText(Utils.getNameToShow(orderList.getModelName()));
                    etVariant.setText(Utils.getNameToShow(orderList.getVariant()));
                    etQuantity.setText(String.valueOf(orderList.getQuantity()));
                    etTotalAmount.setText(String.valueOf(orderList.getAmount()));
                    etAdvanceAmount.setText(String.valueOf(orderList.getAdvancePaid()));
                    etDeliveryDate.setText(Utils.getDateToShowFromFirebase(orderList.getDeliveryDate()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        etBookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(etBookingDate);
            }
        });

        etDeliveryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(etDeliveryDate);
            }
        });

        btplaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (action.equals(Constants.ADD_NEW_ORDER))
                    confirmOrder();
                else if (action.equals(Constants.EDIT_ORDER))
                    confirmOrder();
            }
        });
    }

    private boolean checkData() {

        boolean everythingIsRight = true;

        if (mBillNumber.isEmpty()) {
            etBillNumber.setError(getResources().getString(R.string.error_cannot_be_empty));
            everythingIsRight = false;
        }
        if (mCustomerName.isEmpty() || !Pattern.matches("^[a-zA-Z\\s]*", mCustomerName)) {
            etCustomerName.setError(getString(R.string.error_not_a_valid_name));
            everythingIsRight = false;
        }
        if (mCustomerAddress.isEmpty()) {
            etCustomerAddress.setError(getResources().getString(R.string.error_cannot_be_empty));
            everythingIsRight = false;
        }
        if (!Utils.isPhoneNumberValid(mCustomerPhoneNumber)) {
            etCustomerPhoneNumber.setError(getString(R.string.error_invalid_number));
            everythingIsRight = false;
        }
        if (mModelName.isEmpty()) {
            etModelName.setError(getResources().getString(R.string.error_cannot_be_empty));
            everythingIsRight = false;
        }
        if (mVariant.isEmpty()) {
            etVariant.setError(getResources().getString(R.string.error_cannot_be_empty));
            everythingIsRight = false;
        }
        if (mQuantity <= 0) {
            etQuantity.setError(getString(R.string.error_cant_be_less_than_1));
            everythingIsRight = false;
        }
        if (mTotalAmount <= 0) {
            etTotalAmount.setError(getString(R.string.error_cant_be_less_than_1));
            everythingIsRight = false;
        }
        if (mAdvanceAmount > mTotalAmount) {
            etAdvanceAmount.setError(getString(R.string.error_cannot_be_greater_than_total));
            everythingIsRight = false;
        }
        if (mDeliveryDate.isEmpty()) {
            etDeliveryDate.setError(getString(R.string.error_select_date));
            everythingIsRight = false;
        }

        return everythingIsRight;
    }

    private void getOrderDetail() {
        mBookingDate = etBookingDate.getText().toString();
        mBillNumber = etBillNumber.getText().toString();
        mCustomerName = etCustomerName.getText().toString();
        mCustomerAddress = etCustomerAddress.getText().toString();
        mCustomerPhoneNumber = etCustomerPhoneNumber.getText().toString();
        mModelName = etModelName.getText().toString();
        mVariant = etVariant.getText().toString();
        mDeliveryDate = etDeliveryDate.getText().toString();

        if (!etQuantity.getText().toString().isEmpty())
            mQuantity = Integer.parseInt(etQuantity.getText().toString());

        if (!etTotalAmount.getText().toString().isEmpty())
            mTotalAmount = Long.parseLong(etTotalAmount.getText().toString());

        if (!etAdvanceAmount.getText().toString().isEmpty())
            mAdvanceAmount = Long.parseLong(etAdvanceAmount.getText().toString());

    }

    private void confirmOrder() {

        getOrderDetail();

        if (checkData()) {

            OrderList orderList = new OrderList(Utils.getDateToSave(mBookingDate), mBillNumber, mCustomerName,
                    Utils.formattedPhoneNumber(mCustomerPhoneNumber), mCustomerAddress, mModelName, mVariant,
                    mQuantity, mTotalAmount, mAdvanceAmount, Utils.getDateToSave(mDeliveryDate));

            if (action.equals(Constants.ADD_NEW_ORDER)){
                orderListReference.child(orderListReference.push().getKey()).setValue(orderList);
                Utils.showToast(AddOrder.this, getString(R.string.toast_saved));
            }else {
                orderListReference.child(orderKey).setValue(orderList);
                Utils.showToast(AddOrder.this, getString(R.string.toast_updated));
            }
            finish();
        }
    }

    private void showDatePicker(final EditText etView) {
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR); // current year
        final int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(AddOrder.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        etView.setText(Utils.getDateToShow(year, monthOfYear, dayOfMonth));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
