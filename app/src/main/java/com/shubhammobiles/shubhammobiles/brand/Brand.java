package com.shubhammobiles.shubhammobiles.brand;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.brandmodel.BrandModelActivity;
import com.shubhammobiles.shubhammobiles.model.BrandList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

public class Brand extends Fragment implements BrandRecyclerAdapter.BrandListClickListener {

    private static final String TAG = Brand.class.getSimpleName();
    private static View view;

    private DatabaseReference brandListReference, brandModelReference, variantReference;

    private RecyclerView mRecyclerBrandList;
    private BrandRecyclerAdapter mBrandRecyclerAdapter;

    private boolean mOwner = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nav_stock, container, false);

        brandListReference = FirebaseUtil.getBrandListReference();
        brandModelReference = FirebaseUtil.getBrandModelListReference();
        variantReference = FirebaseUtil.getModelVariantListReference();

        mRecyclerBrandList = (RecyclerView) view.findViewById(R.id.recycler_brand_list);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerBrandList.setLayoutManager(layoutManager);

        mBrandRecyclerAdapter = new BrandRecyclerAdapter(BrandList.class,
                R.layout.list_stock,
                BrandRecyclerAdapter.BrandRecyclerViewHolder.class,
                brandListReference, this);

        mRecyclerBrandList.setAdapter(mBrandRecyclerAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                String owner = mBrandRecyclerAdapter.getItem(viewHolder.getLayoutPosition()).getOwner();

                if (Utils.checkOwner(owner)) {
                    if (swipeDir == ItemTouchHelper.LEFT) {
                        String brandId = mBrandRecyclerAdapter.getRef(viewHolder.getLayoutPosition()).getKey();
                        String brandName = mBrandRecyclerAdapter.getItem(viewHolder.getLayoutPosition()).getBrandName();
                        buildAlert(viewHolder, brandId, brandName);
                    }
                } else {
                    Utils.showToast(getContext(), "You are not the owner");
                    mBrandRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }

            }
        }).attachToRecyclerView(mRecyclerBrandList);

        return view;
    }

    @Override
    public void onBrandListItemClick(int clickedItemIndex) {
        mOwner = Utils.checkOwner(mBrandRecyclerAdapter.getItem(clickedItemIndex).getOwner());
        Intent intent = new Intent(getContext(), BrandModelActivity.class);

        intent.putExtra(Constants.KEY_BRAND_NAME, mBrandRecyclerAdapter.getItem(clickedItemIndex).getBrandName());
        intent.putExtra(Constants.KEY_BRAND_KEY, mBrandRecyclerAdapter.getRef(clickedItemIndex).getKey());
        intent.putExtra(Constants.KEY_AM_I_OWNER, mOwner);

        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBrandRecyclerAdapter.cleanup();
    }

    private void removeItem(String brandId) {
        brandListReference.child(brandId).removeValue();
        brandModelReference.child(brandId).removeValue();
        variantReference.child(brandId).removeValue();
    }

    public void buildAlert(final RecyclerView.ViewHolder viewHolder, final String brandId, String brandName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomTheme_Dialog)
                .setTitle(getString(R.string.alert_remove) + " " + brandName)
                .setMessage(getString(R.string.alert_are_u_sure_remove_msg) + " " + brandName + " ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(brandId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mBrandRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
