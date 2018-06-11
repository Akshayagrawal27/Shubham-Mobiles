package com.shubhammobiles.shubhammobiles.price;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.BrandList;
import com.shubhammobiles.shubhammobiles.model.BrandModelList;
import com.shubhammobiles.shubhammobiles.model.PriceList;
import com.shubhammobiles.shubhammobiles.model.VariantList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

import java.util.ArrayList;

public class CopyDialogFragment extends DialogFragment implements CopyPasteBrandRecyclerAdapter.CopyPasteBrandClickListener,
        CopyPasteBrandModelRecyclerAdapter.CopyPasteBrandModelClickListener,
        CopyPasteVariantRecyclerAdapter.CopyPasteVariantClickListener{

    String clickedBrandKey, clickedBrandModelKey, clickedVariantKey, variantKey;

    RecyclerView mCopyRecyclerView;

    CopyPasteBrandRecyclerAdapter mCopyPasteBrandRecyclerAdapter;
    CopyPasteBrandModelRecyclerAdapter mCopyPasteBrandModelRecyclerAdapter;
    CopyPasteVariantRecyclerAdapter mCopyPasteVariantRecyclerAdapter;

    ArrayList<PriceList> priceToCopy;
    DatabaseReference brandListReference, brandModelReference, variantReference, priceListReference;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     * @param priceToCopy
     * @param variantKey
     */
    public static CopyDialogFragment newInstance(ArrayList<PriceList> priceToCopy, String variantKey) {
        CopyDialogFragment addListDialogFragment = new CopyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Hashmap", priceToCopy);
        bundle.putString(Constants.KEY_VARIANT_KEY, variantKey);
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        priceToCopy =(ArrayList<PriceList>) getArguments().getSerializable("Hashmap");
        variantKey = getArguments().getString(Constants.KEY_VARIANT_KEY);
    }

    /**
     * Open the keyboard automatically when the dialog fragment is opened
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        // Get the layout inflater
        //priceReference = FirebaseUtil.getPriceListReference().child(variantKey).child(clickedPriceKey);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_copy_price, null);

        mCopyRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_copy_paste);

        brandListReference = FirebaseUtil.getBrandListReference();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mCopyRecyclerView.setLayoutManager(layoutManager);

        mCopyPasteBrandRecyclerAdapter = new CopyPasteBrandRecyclerAdapter(BrandList.class,
                R.layout.list_copy_paste,
                CopyPasteBrandRecyclerAdapter.CopyPasteBrandRecyclerViewHolder.class,
                brandListReference, this);

        mCopyRecyclerView.setAdapter(mCopyPasteBrandRecyclerAdapter);

        Log.wtf("priceActivity", priceToCopy.toString());
        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    @Override
    public void onCopyPasteBrandClick(int clickedItemIndex) {

        clickedBrandKey = mCopyPasteBrandRecyclerAdapter.getRef(clickedItemIndex).getKey();
        brandModelReference = FirebaseUtil.getBrandModelListReference()
                .child(clickedBrandKey);

        mCopyPasteBrandModelRecyclerAdapter = new CopyPasteBrandModelRecyclerAdapter(BrandModelList.class,
                R.layout.list_copy_paste,
                CopyPasteBrandModelRecyclerAdapter.CopyPasteBrandModelRecyclerViewHolder.class,
                brandModelReference, this);

        mCopyRecyclerView.setAdapter(mCopyPasteBrandModelRecyclerAdapter);
    }

    @Override
    public void onCopyPasteBrandModelClick(int clickedItemIndex) {

        clickedBrandModelKey = mCopyPasteBrandModelRecyclerAdapter.getRef(clickedItemIndex).getKey();
        variantReference = FirebaseUtil.getModelVariantListReference().child(clickedBrandKey).child(clickedBrandModelKey);

        mCopyPasteVariantRecyclerAdapter = new CopyPasteVariantRecyclerAdapter(VariantList.class,
                R.layout.list_copy_paste,
                CopyPasteVariantRecyclerAdapter.CopyPasteVariantRecyclerViewHolder.class,
                variantReference, this, variantKey);

        mCopyRecyclerView.setAdapter(mCopyPasteVariantRecyclerAdapter);
    }

    @Override
    public void onCopyPasteVariantClick(int clickedItemIndex) {

        clickedVariantKey = mCopyPasteVariantRecyclerAdapter.getRef(clickedItemIndex).getKey();
        String variantName = mCopyPasteVariantRecyclerAdapter.getItem(clickedItemIndex).getVariantName();

        buildAlert(variantName);
    }

    public void buildAlert(String variantName){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog)
                .setTitle(getString(R.string.alert_copy_price))
                .setMessage(getString(R.string.alert_are_u_sure_copy_price, variantName))
                .setPositiveButton(android.R.string.paste, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updatePrice();
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

    private void updatePrice() {

        priceListReference = FirebaseUtil.getPriceListReference().child(clickedVariantKey);

        for (int i=0;i<priceToCopy.size();i++){
            final int finalI = i;
            priceListReference.child(priceListReference.push().getKey()).setValue(priceToCopy.get(i))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful() && finalI == priceToCopy.size()){
                                Utils.showToast(getActivity(), "Copied");
                            }
                        }
                    });
        }
    }
}

