package com.shubhammobiles.shubhammobiles.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.shubhammobiles.shubhammobiles.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by Akshay on 20-02-2018.
 */

public class Utils {

    private static String[] monthName = {"January","February","March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};

    public static boolean isPhoneNumberValid(String phoneNumber){
        if(Pattern.matches("[0-9]{10}", phoneNumber)){
            return true;
        }else
            return false;
    }

    public static String formattedPhoneNumber(String phoneNumber) {
        return "+91" + phoneNumber;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void updateCurrentTimeStamp(String brandKey){
        HashMap<String, Object> updateTime = new HashMap<String, Object>();
        updateTime.put("/" + Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED + "/" +
                Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        FirebaseUtil.getBrandListReference().child(brandKey).updateChildren(updateTime);
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static String getCurrentDate(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        final int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        return  getDateToShow(mYear, mMonth, mDay) ;
    }

    public static String getDateToShow(int mYear, int mMonth, int mDay) {
        String month = monthName[mMonth].substring(0,3);

        String day;
        if (mDay < 10){
            day = "0" + mDay;
        }else
            day = String.valueOf(mDay);
        return String.valueOf(month + " " + day + ", " + mYear);
    }

    public static String  getDateToSave(String date){

        if (date == null)
            return null;

        String month = date.substring(0,3);
        String day = date.substring(4,6);
        String year = date.substring(8, 12);

        String mMonth = "";
        for(int i = 1; i<= monthName.length;i++) {
            if(monthName[i-1].contains(month)) {
                if (i < 10){
                    mMonth = "0" + i;
                }else
                    mMonth = String.valueOf(i);
            }
        }
        return year + "/" + mMonth + "/" + day;
    }

    public static String getDateToShowFromFirebase(String date) {

        if (date == null){
            return date;
        }
        String year = date.substring(0,4);
        String monthInNum = date.substring(6, 7);
        String day = date.substring(8, 10);

        String month = monthName[Integer.parseInt(monthInNum)-1].substring(0,3);
        return month + " " + day + ", " + year;
    }

    public static String getNameToShow(String str){

        if (str == null){
            return str;
        }
        String[] words = str.split(" ");

        String text = "";
        for(String word: words) {
            if(Pattern.matches("[a-zA-Z]*", word)) {
                text += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
            }else
                text += word + " ";
        }
        return text.substring(0, text.length()-1);
    }

    public static String setCustomerNameOnView(String name){

        if (name == null){
            return name;
        }
        String flag = "";
        String nameToPrint = "";
        String[] arr = name.split(" ");

        for(String str: arr) {
            flag += str;
            if(flag.length() < 19) {
                nameToPrint += str + " ";
            }else {
                flag = "";
                nameToPrint += "\n" + str;
            }
        }
        return nameToPrint;
    }

    public static void callToThisNumber(Context context, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber.substring(3)));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.wtf("phoneActivity", "CallToThisNumber: Permission Denied");
            return;
        }
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(callIntent);
    }

    public static boolean checkOwner(String owner) {

        if (owner.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return true;
        }else
            return false;
    }
}
