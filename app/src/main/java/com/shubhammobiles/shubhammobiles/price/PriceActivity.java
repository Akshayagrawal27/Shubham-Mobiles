package com.shubhammobiles.shubhammobiles.price;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.BaseActivity;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.PriceList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;

import java.util.ArrayList;

public class PriceActivity extends BaseActivity implements PriceRecyclerAdapter.PriceListClickListener{

    RecyclerView mRecyclerPriceList;
    DatabaseReference priceListReference, variantReference;

    ValueEventListener priceListReferenceListener, priceReferenceListener;
    PriceRecyclerAdapter mPriceRecyclerAdapter;

    String variantKey, variantName, brandModelKey, brandKey;

    private String bestPriceKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        Intent intent = this.getIntent();
        variantName = intent.getStringExtra(Constants.KEY_VARIANT_NAME);
        setTitle(variantName);

        variantKey = intent.getStringExtra(Constants.KEY_VARIANT_KEY);
        brandModelKey = intent.getStringExtra(Constants.KEY_BRAND_MODEL_KEY);
        brandKey = intent.getStringExtra(Constants.KEY_BRAND_KEY);
        bestPriceKey = intent.getStringExtra("bestPriceKey");

        variantReference = FirebaseUtil.getModelVariantListReference().child(brandKey).child(brandModelKey);
        priceListReference = FirebaseUtil.getPriceListReference().child(variantKey);

        mRecyclerPriceList = (RecyclerView) findViewById(R.id.recycler_price_list);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerPriceList.setLayoutManager(layoutManager);

        mPriceRecyclerAdapter = new PriceRecyclerAdapter(PriceList.class,
                R.layout.list_price,
                PriceRecyclerAdapter.PriceRecyclerViewHolder.class,
                priceListReference, this, bestPriceKey);

        mRecyclerPriceList.setAdapter(mPriceRecyclerAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.LEFT){
                    String priceId = mPriceRecyclerAdapter.getRef(viewHolder.getLayoutPosition()).getKey();
                    String shopName = mPriceRecyclerAdapter.getItem(viewHolder.getLayoutPosition()).getShopName();
                    buildAlert(viewHolder, priceId, shopName);
                }
            }
        }).attachToRecyclerView(mRecyclerPriceList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_price, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_copy){

            final ArrayList<PriceList> priceToCopy = new ArrayList<>();

            priceListReferenceListener = priceListReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot!=null){

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            PriceList priceList = (PriceList) snapshot.getValue(PriceList.class);
                            priceToCopy.add(priceList);
                            Log.wtf("priceActivity", snapshot.getKey());
                        }
                        DialogFragment dialog = CopyDialogFragment.newInstance(priceToCopy, variantKey);
                        dialog.show(getFragmentManager(), Constants.KEY_DIALOG_ADD_MODEL_VARIANT);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onPriceListLongClick(int clickedItemIndex) {
        String clickedPriceKey = mPriceRecyclerAdapter.getRef(clickedItemIndex).getKey();
        DialogFragment dialog = EditPriceDialogFragment.newInstance(variantKey, clickedPriceKey);
        dialog.show(getFragmentManager(), Constants.KEY_DIALOG_EDIT_PRICE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (priceReferenceListener != null){
            priceListReference.removeEventListener(priceReferenceListener);
        }
        if (priceListReferenceListener != null){
            priceListReference.removeEventListener(priceListReferenceListener);
        }
    }

    private void removeItem(String priceId) {
        priceListReference.child(priceId).removeValue();
    }

    public void buildAlert(final RecyclerView.ViewHolder viewHolder, final String priceId, String shopName){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomTheme_Dialog)
                .setTitle(getString(R.string.alert_remove_price))
                .setMessage(getString(R.string.alert_are_u_sure_remove_price, shopName))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(priceId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mPriceRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
