package com.shubhammobiles.shubhammobiles.order;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

import java.util.HashMap;

public class OrderDetail extends BaseActivity {

    private TextView tvBookingDate, tvBillNumber, tvCustomerName, tvCustomerAddress, tvCustomerPhoneNumber, tvModelName, tvVariant;
    private TextView tvQuantity, tvTotalAmount, tvAdvanceAmount, tvPendingAmount, tvDeliveryDate, tvOrderStatus;
    private static final int REQUEST_CALL_PHONE = 1;

    private DatabaseReference orderListReference;

    String orderKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if (Build.VERSION.SDK_INT >=23){
            // Permission is not granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        }

        Intent intent = this.getIntent();
        orderKey = intent.getStringExtra(Constants.KEY_ORDER);

        orderListReference = FirebaseUtil.getOrderListReference();

        tvBookingDate = (TextView) findViewById(R.id.tv_order_booking_date);
        tvBillNumber = (TextView) findViewById(R.id.tv_order_bill_number);
        tvCustomerName = (TextView) findViewById(R.id.tv_order_customer_name);
        tvCustomerAddress = (TextView) findViewById(R.id.tv_order_customer_address);
        tvCustomerPhoneNumber = (TextView) findViewById(R.id.tv_order_customer_phone_number);
        tvModelName = (TextView) findViewById(R.id.tv_order_model_name);
        tvVariant = (TextView) findViewById(R.id.tv_order_variant);
        tvQuantity = (TextView) findViewById(R.id.tv_order_quantity);
        tvTotalAmount = (TextView) findViewById(R.id.tv_order_amount);
        tvAdvanceAmount = (TextView) findViewById(R.id.tv_order_advance_amount);
        tvPendingAmount = (TextView) findViewById(R.id.tv_order_pending_amount);
        tvDeliveryDate = (TextView) findViewById(R.id.tv_order_delivery_date);
        tvOrderStatus = (TextView) findViewById(R.id.tv_order_status);

        tvCustomerPhoneNumber.setPaintFlags(tvCustomerPhoneNumber.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);

        tvCustomerPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    Utils.callToThisNumber(getBaseContext(), tvCustomerPhoneNumber.getText().toString());
                else {
                    ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CALL_PHONE){

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

            } else{
                Utils.showToast(this, "Permission Denied");
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        orderListReference.child(orderKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OrderList orderList = dataSnapshot.getValue(OrderList.class);
                setTitle(getString(R.string.title_order_number) + orderList.getBillNumber());

                tvBookingDate.setText(Utils.getDateToShowFromFirebase(orderList.getBookingDate()));
                tvBillNumber.setText(orderList.getBillNumber());
                tvCustomerName.setText(Utils.setCustomerNameOnView(Utils.getNameToShow(orderList.getCustomerName())));
                tvCustomerAddress.setText(Utils.getNameToShow(orderList.getCustomerAddress()));
                tvCustomerPhoneNumber.setText(orderList.getCustomerPhoneNumber());
                tvModelName.setText(Utils.getNameToShow(orderList.getModelName()));
                tvVariant.setText(Utils.getNameToShow(orderList.getVariant()));
                tvQuantity.setText(String.valueOf(orderList.getQuantity()));
                tvTotalAmount.setText("\u20B9 " + String.valueOf(orderList.getAmount()));
                tvAdvanceAmount.setText("\u20B9 " + String.valueOf(orderList.getAdvancePaid()));
                tvPendingAmount.setText("\u20B9 " + String.valueOf(orderList.getDueAmount()));
                tvDeliveryDate.setText(Utils.getDateToShowFromFirebase(orderList.getDeliveryDate()));
                tvOrderStatus.setText(Utils.getNameToShow(orderList.getStatus()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tvOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tvOrderStatus.getText().toString().toLowerCase().equals(Constants.ORDER_STATUS_PENDING)) {
                    buildAlert(orderKey);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, AddOrder.class);
            intent.setAction(Constants.EDIT_ORDER);
            intent.putExtra(Constants.KEY_ORDER, orderKey);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void buildAlert(final String orderKey) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomTheme_Dialog)
                .setTitle(getString(R.string.alert_change_order_status))
                .setMessage(getString(R.string.alert_are_u_sure_status_completed))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        HashMap<String, Object> updateOrderStatus = new HashMap<String, Object>();
                        updateOrderStatus.put("/" + Constants.FIREBASE_PROPERTY_ORDER_STATUS, Constants.ORDER_STATUS_COMPLETED);
                        orderListReference.child(orderKey).updateChildren(updateOrderStatus);
                        tvOrderStatus.setText(Utils.getNameToShow(Constants.ORDER_STATUS_COMPLETED));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
