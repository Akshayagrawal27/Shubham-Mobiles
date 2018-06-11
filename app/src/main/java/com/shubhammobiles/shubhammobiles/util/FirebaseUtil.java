package com.shubhammobiles.shubhammobiles.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.model.AccountList;
import com.shubhammobiles.shubhammobiles.model.BrandList;
import com.shubhammobiles.shubhammobiles.model.User;

import java.util.HashMap;

/**
 * Created by Akshay on 04-03-2018.
 */

public class FirebaseUtil {

    public static DatabaseReference getBrandListReference(){
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_BRAND_LIST)
                .child(FirebaseAuth.getInstance().getUid());
    }

    public static DatabaseReference getBrandModelListReference(){
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_BRAND_MODEL_LIST);
    }

    public static DatabaseReference getModelVariantListReference(){
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_MODEL_VARIANT_LIST);
    }

    public static DatabaseReference getOrderListReference(){
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_ORDER_LIST)
                .child(FirebaseAuth.getInstance().getUid());
    }

    public static DatabaseReference getAccountListReference(){
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_ACCOUNT_LIST)
                .child(FirebaseAuth.getInstance().getUid());
    }

    public static DatabaseReference getNoteListReference(){
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_NOTE_LIST);
    }

    public static DatabaseReference getPriceListReference() {
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_PRICE_LIST)
                .child(FirebaseAuth.getInstance().getUid());
    }

    public static String getUserPhoneNumber(){
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }

    public static DatabaseReference getUserListReference() {
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_USERS);
    }

    public static DatabaseReference getSharedBrandWithReference(String brandKey) {
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_SHARED_BRAND_WITH).child(brandKey);
    }

    public static DatabaseReference getSharedAccountWithReference(String accountKey) {
        return FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LOCATION_SHARED_ACCOUNT_WITH).child(accountKey);
    }

    public static void shareBrand(boolean share, final User friend, final String friendKey,
                                  final String brandKey, final DatabaseReference.CompletionListener completionListener) {

        if (share){
            getBrandListReference().child(brandKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    BrandList brandList = null;
                    if (dataSnapshot.getValue() != null){
                        brandList = dataSnapshot.getValue(BrandList.class);
                    }
                    if (brandList != null){
                        brandList.setOwner(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        HashMap<String, Object> shareList = new HashMap<>();
                        shareList.put(Constants.FIREBASE_LOCATION_BRAND_LIST + "/" + friendKey + "/" +  brandKey, brandList);
                        shareList.put(Constants.FIREBASE_LOCATION_SHARED_BRAND_WITH + "/" + brandKey + "/" + friendKey, friend);
                        FirebaseDatabase.getInstance().getReference().updateChildren(shareList, completionListener);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            HashMap<String, Object> shareList = new HashMap<>();
            shareList.put(Constants.FIREBASE_LOCATION_BRAND_LIST + "/" + friendKey + "/" +  brandKey, null);
            shareList.put(Constants.FIREBASE_LOCATION_SHARED_BRAND_WITH + "/" + brandKey + "/" + friendKey, null);
            FirebaseDatabase.getInstance().getReference().updateChildren(shareList, completionListener);
        }
    }

    public static void shareAccount(boolean share, final User friend, final String friendKey,
                                  final String accountKey, final DatabaseReference.CompletionListener completionListener) {

        if (share){
            getAccountListReference().child(accountKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    AccountList accountList = null;
                    if (dataSnapshot.getValue() != null){
                        accountList = dataSnapshot.getValue(AccountList.class);
                    }

                    if (accountList != null){
                        accountList.setOwner(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        HashMap<String, Object> shareList = new HashMap<>();
                        shareList.put(Constants.FIREBASE_LOCATION_ACCOUNT_LIST + "/" + friendKey + "/" +  accountKey, accountList);
                        shareList.put(Constants.FIREBASE_LOCATION_SHARED_ACCOUNT_WITH + "/" + accountKey + "/" + friendKey, friend);
                        FirebaseDatabase.getInstance().getReference().updateChildren(shareList, completionListener);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            HashMap<String, Object> shareList = new HashMap<>();
            shareList.put(Constants.FIREBASE_LOCATION_ACCOUNT_LIST + "/" + friendKey + "/" +  accountKey, null);
            shareList.put(Constants.FIREBASE_LOCATION_SHARED_ACCOUNT_WITH + "/" + accountKey + "/" + friendKey, null);
            FirebaseDatabase.getInstance().getReference().updateChildren(shareList, completionListener);
        }
    }
}
