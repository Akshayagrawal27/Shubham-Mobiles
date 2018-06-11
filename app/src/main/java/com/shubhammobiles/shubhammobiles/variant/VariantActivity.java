package com.shubhammobiles.shubhammobiles.variant;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.BaseActivity;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.BrandModelList;
import com.shubhammobiles.shubhammobiles.model.PriceList;
import com.shubhammobiles.shubhammobiles.model.VariantList;
import com.shubhammobiles.shubhammobiles.price.PriceActivity;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class VariantActivity extends BaseActivity implements VariantRecyclerAdapter.VariantListClickListener {

    private DatabaseReference variantReference, brandModelReference, priceListReference;

    private RecyclerView mRecyclerVariantList;
    private VariantRecyclerAdapter mVariantRecyclerAdapter;
    private ValueEventListener variantReferenceListener, priceReferenceListener;

    public String brandKey, brandModelName, brandModelKey;

    public ArrayList<String> variantName = new ArrayList<>();
    public ArrayList<String> variantKey = new ArrayList<>();

    private String bestPriceKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variant);

        Constants.REMOVAL_TAG = 1;

        Intent intent = this.getIntent();
        brandModelName = intent.getStringExtra(Constants.KEY_BRAND_MODEL_NAME);
        setTitle(brandModelName);
        brandModelKey = intent.getStringExtra(Constants.KEY_BRAND_MODEL_KEY);
        brandKey = intent.getStringExtra(Constants.KEY_BRAND_KEY);

        variantReference = FirebaseUtil.getModelVariantListReference().child(brandKey).child(brandModelKey);
        brandModelReference = FirebaseUtil.getBrandModelListReference().child(brandKey);
        priceListReference = FirebaseUtil.getPriceListReference();

        mRecyclerVariantList = (RecyclerView) findViewById(R.id.recycler_variant_item_list);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerVariantList.setLayoutManager(layoutManager);

        mVariantRecyclerAdapter = new VariantRecyclerAdapter(VariantList.class,
                R.layout.list_model_variant,
                VariantRecyclerAdapter.VariantItemRecyclerViewHolder.class,
                variantReference, brandKey, this);

        mRecyclerVariantList.setAdapter(mVariantRecyclerAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.LEFT) {
                    String variantId = mVariantRecyclerAdapter.getRef(viewHolder.getLayoutPosition()).getKey();
                    buildAlert(viewHolder, variantId);
                }
            }
        }).attachToRecyclerView(mRecyclerVariantList);
    }

    @Override
    protected void onStart() {
        super.onStart();

        variantReferenceListener = variantReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalQty = 0;
                variantName.clear();
                variantKey.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    VariantList stockListItem = snapshot.getValue(VariantList.class);
                    variantName.add(stockListItem.getVariantName());
                    variantKey.add(snapshot.getKey());
                    totalQty += stockListItem.getVariantQuantity();
                    final String variantKey = snapshot.getKey();

                    priceReferenceListener = priceListReference.child(variantKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long bestPrice = 0;
                            if (Constants.REMOVAL_TAG == 0)
                                return;

                            for (DataSnapshot snap : dataSnapshot.getChildren()) {

                                PriceList priceList = snap.getValue(PriceList.class);
                                if (bestPrice == 0) {
                                    bestPrice = priceList.getVariantPrice();
                                    bestPriceKey = snap.getKey();
                                    continue;
                                } else if (bestPrice > priceList.getVariantPrice() && priceList.getVariantPrice() != 0) {
                                    bestPrice = priceList.getVariantPrice();
                                    bestPriceKey=snap.getKey();
                                }
                            }

                            HashMap<String, Object> updateBestPrice = new HashMap<String, Object>();
                            updateBestPrice.put("/" + Constants.FIREBASE_PROPERTY_VARIANT_BEST_PRICE, bestPrice);
                            variantReference.child(variantKey).updateChildren(updateBestPrice)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mRecyclerVariantList.setAdapter(mVariantRecyclerAdapter);
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if (priceReferenceListener != null)
                    priceListReference.removeEventListener(priceReferenceListener);

                brandModelReference.child(brandModelKey).setValue(new BrandModelList(brandModelName, totalQty));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_variant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add_price) {
            if (!variantName.isEmpty() && !variantKey.isEmpty()) {
                DialogFragment dialog = AddPriceDialogFragment.newInstance(variantName, variantKey, brandKey, brandModelKey);
                dialog.show(getFragmentManager(), Constants.KEY_DIALOG_ADD_PRICE);
                return true;
            } else
                Utils.showToast(this, "Add Variant");
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (variantReferenceListener != null) {
            variantReference.removeEventListener(variantReferenceListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVariantRecyclerAdapter.cleanup();
    }

    public void showAddListDialog(View view) {
        DialogFragment dialog = AddVariantDialogFragment.newInstance(brandKey, brandModelKey);
        dialog.show(getFragmentManager(), Constants.KEY_DIALOG_ADD_MODEL_VARIANT);
    }

    private void removeItem(final String variantId) {
        Constants.REMOVAL_TAG = 0;
        priceListReference.child(variantId).removeValue();
        variantReference.child(variantId).removeValue();
    }

    public void buildAlert(final RecyclerView.ViewHolder viewHolder, final String variantId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomTheme_Dialog)
                .setTitle(getString(R.string.alert_remove_item))
                .setMessage(getString(R.string.alert_are_u_sure_remove_item))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        removeItem(variantId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mVariantRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onVariantListItemClick(final int clickedItemIndex) {
        DatabaseReference priceListReference;
        priceListReference = FirebaseUtil.getPriceListReference().child(mVariantRecyclerAdapter.getRef(clickedItemIndex).getKey());

        priceListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    Intent intent = new Intent(getBaseContext(), PriceActivity.class);
                    intent.putExtra(Constants.KEY_VARIANT_KEY, mVariantRecyclerAdapter.getRef(clickedItemIndex).getKey());
                    intent.putExtra(Constants.KEY_VARIANT_NAME, mVariantRecyclerAdapter.getItem(clickedItemIndex).getVariantName());
                    intent.putExtra(Constants.KEY_BRAND_MODEL_KEY, brandModelKey);
                    intent.putExtra(Constants.KEY_BRAND_KEY, brandKey);
                    intent.putExtra("bestPriceKey", bestPriceKey);
                    startActivity(intent);
                } else
                    Utils.showToast(getBaseContext(), getString(R.string.toast_no_price_added));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
