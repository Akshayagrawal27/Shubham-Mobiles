package com.shubhammobiles.mobilebeta.price;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.google.firebase.database.DatabaseReference;
import com.shubhammobiles.mobilebeta.BaseActivity;
import com.shubhammobiles.mobilebeta.R;
import com.shubhammobiles.mobilebeta.model.PriceList;
import com.shubhammobiles.mobilebeta.util.Constants;
import com.shubhammobiles.mobilebeta.util.FirebaseUtil;

public class PriceActivity extends BaseActivity implements PriceRecyclerAdapter.PriceListClickListener{

    RecyclerView mRecyclerPriceList;
    DatabaseReference priceListReference;
    PriceRecyclerAdapter mPriceRecyclerAdapter;

    String variantKey, variantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        Intent intent = this.getIntent();
        variantName = intent.getStringExtra(Constants.KEY_VARIANT_NAME);
        setTitle(variantName);
        variantKey = intent.getStringExtra(Constants.KEY_VARIANT_KEY);

        priceListReference = FirebaseUtil.getPriceListReference().child(variantKey);

        mRecyclerPriceList = (RecyclerView) findViewById(R.id.recycler_price_list);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerPriceList.setLayoutManager(layoutManager);

        mPriceRecyclerAdapter = new PriceRecyclerAdapter(PriceList.class,
                R.layout.list_price,
                PriceRecyclerAdapter.PriceRecyclerViewHolder.class,
                priceListReference, this);

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
    public void onPriceListLongClick(int clickedItemIndex) {
        String clickedPriceKey = mPriceRecyclerAdapter.getRef(clickedItemIndex).getKey();
        DialogFragment dialog = EditPriceDialogFragment.newInstance(variantKey, clickedPriceKey);
        dialog.show(getFragmentManager(), Constants.KEY_DIALOG_EDIT_PRICE);
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
