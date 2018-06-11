package com.shubhammobiles.shubhammobiles;

import android.*;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.shubhammobiles.shubhammobiles.accounts.Accounts;
import com.shubhammobiles.shubhammobiles.accounts.AddAccountDialogFragment;
import com.shubhammobiles.shubhammobiles.brand.AddBrandDialogFragment;
import com.shubhammobiles.shubhammobiles.brand.Brand;
import com.shubhammobiles.shubhammobiles.order.AddOrder;
import com.shubhammobiles.shubhammobiles.order.Order;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.Utils;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private Fragment fragment;
    private FragmentManager fragmentManager;

    private final int REQUEST_CALL_PHONE = 1;

    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = new Brand();
        //Launch Fragment for the First Time
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_container, fragment).addToBackStack(null).commit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (Build.VERSION.SDK_INT >=23){
            // Permission is not granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_stock:
                    fragment = new Brand();
                    break;
                case R.id.navigation_order:
                    fragment = new Order();
                    break;

                case R.id.navigation_accounts:
                    fragment = new Accounts();
                    break;
            }
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, fragment).commit();
            return true;
        }
    };

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
    public void onBackPressed() {
        if(fragment instanceof Brand){
            finish();
        }else{
            navigation.setSelectedItemId(R.id.navigation_stock);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            return true;
        }
        return false;
    }

    public void showAddListDialog(View view) {
        DialogFragment dialog = AddBrandDialogFragment.newInstance();
        dialog.show(getFragmentManager(), Constants.KEY_DIALOG_ADD_BRAND);
    }

    public void AddOrderDetails(View view) {
        Intent intent = new Intent(MainActivity.this, AddOrder.class);
        intent.setAction(Constants.ADD_NEW_ORDER);
        startActivity(intent);
    }

    public void showAddAccountDialog(View view) {
        DialogFragment dialog = AddAccountDialogFragment.newInstance();
        dialog.show(getFragmentManager(), Constants.KEY_DIALOG_ADD_ACCOUNT);
    }
}
