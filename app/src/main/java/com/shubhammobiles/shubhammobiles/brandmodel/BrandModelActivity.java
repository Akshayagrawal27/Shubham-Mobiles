package com.shubhammobiles.shubhammobiles.brandmodel;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.UniversalTimeScale;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.BaseActivity;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.Sharing.ShareActivity;
import com.shubhammobiles.shubhammobiles.model.BrandList;
import com.shubhammobiles.shubhammobiles.model.BrandModelList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;
import com.shubhammobiles.shubhammobiles.variant.VariantActivity;

import java.util.HashMap;

public class BrandModelActivity extends BaseActivity implements BrandModelRecyclerAdapter.BrandModelListClickListener {

    private static final String TAG = BrandModelActivity.class.getSimpleName();

    private DatabaseReference brandModelReference, variantReference, brandReference;

    private RecyclerView mRecyclerBrandModelList;
    private BrandModelRecyclerAdapter mBrandModelRecyclerAdapter;
    private ValueEventListener brandModelReferenceListener;
    private String brandName, brandKey;
    private boolean mOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_model);

        Intent intent = this.getIntent();

        brandName = intent.getStringExtra(Constants.KEY_BRAND_NAME);
        setTitle(brandName);
        brandKey = intent.getStringExtra(Constants.KEY_BRAND_KEY);

        setTitle(brandName);
        brandKey = intent.getStringExtra(Constants.KEY_BRAND_KEY);
        mOwner = intent.getBooleanExtra(Constants.KEY_AM_I_OWNER, false);

        brandReference = FirebaseUtil.getBrandListReference();
        brandModelReference = FirebaseUtil.getBrandModelListReference().child(brandKey);
        variantReference = FirebaseUtil.getModelVariantListReference().child(brandKey);

        mRecyclerBrandModelList = (RecyclerView) findViewById(R.id.recycler_brand_model_list);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerBrandModelList.setLayoutManager(layoutManager);

        mBrandModelRecyclerAdapter = new BrandModelRecyclerAdapter(BrandModelList.class,
                R.layout.list_brand_model,
                BrandModelRecyclerAdapter.BrandModelRecyclerViewHolder.class,
                brandModelReference, this);

        mRecyclerBrandModelList.setAdapter(mBrandModelRecyclerAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                if (mOwner) {
                    if (swipeDir == ItemTouchHelper.LEFT) {
                        String brandModelId = mBrandModelRecyclerAdapter.getRef(viewHolder.getLayoutPosition()).getKey();
                        String brandModelName = mBrandModelRecyclerAdapter.getItem(viewHolder.getLayoutPosition()).getBrandModelName();
                        buildAlert(viewHolder, brandModelId, brandModelName);
                    }
                } else {
                    Utils.showToast(getBaseContext(), "You are not the owner");
                    mBrandModelRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }

            }
        }).attachToRecyclerView(mRecyclerBrandModelList);
    }

    @Override
    protected void onStart() {
        super.onStart();

        brandModelReferenceListener = brandModelReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    int totalQty = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BrandModelList brandModelList = snapshot.getValue(BrandModelList.class);
                        totalQty += brandModelList.getBrandModelQty();
                    }
                    HashMap<String, Object> quantityToUpdate = new HashMap<>();
                    quantityToUpdate.put("/" + brandKey + "/" + Constants.FIREBASE_PROPERTY_BRAND_QTY, totalQty);
                    brandReference.updateChildren(quantityToUpdate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        brandModelReference.removeEventListener(brandModelReferenceListener);
    }

    public void showAddListDialog(View view) {
        DialogFragment dialog = AddBrandModelDialogFragment.newInstance(brandKey);
        dialog.show(getFragmentManager(), Constants.KEY_DIALOG_ADD_BRAND_MODEL);
    }

    @Override
    public void onBrandModelListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, VariantActivity.class);
        intent.putExtra(Constants.KEY_BRAND_MODEL_NAME, mBrandModelRecyclerAdapter.getItem(clickedItemIndex).getBrandModelName());
        intent.putExtra(Constants.KEY_BRAND_MODEL_KEY, mBrandModelRecyclerAdapter.getRef(clickedItemIndex).getKey());
        intent.putExtra(Constants.KEY_BRAND_KEY, brandKey);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBrandModelRecyclerAdapter.cleanup();
    }

    private void removeItem(String brandModelId) {
        brandModelReference.child(brandModelId).removeValue();
        variantReference.child(brandModelId).removeValue();
    }

    public void buildAlert(final RecyclerView.ViewHolder viewHolder, final String id, String brandModelName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomTheme_Dialog)
                .setTitle(getString(R.string.alert_remove) + " " + brandModelName)
                .setMessage(getString(R.string.alert_are_u_sure_remove_msg) + " " + brandModelName + " ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(id);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mBrandModelRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_brand_model, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_share_brand){
            showSearchUserDialog();
            return true;
        }
        return false;
    }

    private void showSearchUserDialog() {
        if (mOwner){
            Intent intent = new Intent(this, ShareActivity.class);
            intent.setAction(Constants.SHARE_STOCK);
            intent.putExtra(Constants.KEY_BRAND_KEY, brandKey);
            intent.putExtra(Constants.KEY_BRAND_NAME, brandName);
            startActivity(intent);
        }else{
            Utils.showToast(this, "Please Contact owner of this list to share");
        }

    }
}
