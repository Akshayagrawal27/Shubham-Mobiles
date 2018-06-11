package com.shubhammobiles.shubhammobiles.price;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.PriceList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

public class EditPriceDialogFragment extends DialogFragment {
    String variantKey,clickedPriceKey;
    EditText mEditTextShopName, mEditTextItemQty;

    DatabaseReference priceReference;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static EditPriceDialogFragment newInstance(String variantKey, String clickedPriceKey) {
        EditPriceDialogFragment addListDialogFragment = new EditPriceDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_VARIANT_KEY, variantKey);
        bundle.putString(Constants.KEY_PRICE_KEY, clickedPriceKey);
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
}

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        variantKey = getArguments().getString(Constants.KEY_VARIANT_KEY);
        clickedPriceKey = getArguments().getString(Constants.KEY_PRICE_KEY);
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
        priceReference = FirebaseUtil.getPriceListReference().child(variantKey).child(clickedPriceKey);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_list, null);
        mEditTextShopName = (EditText) rootView.findViewById(R.id.edit_text_list_name);
        mEditTextItemQty = (EditText) rootView.findViewById(R.id.edit_text_list_qty);

        mEditTextShopName.setHint(getResources().getString(R.string.hint_add_shop_name));
        mEditTextItemQty.setHint(getResources().getString(R.string.hint_add_price));
        mEditTextItemQty.setVisibility(View.VISIBLE);

        priceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null){
                    PriceList priceList = dataSnapshot.getValue(PriceList.class);
                    mEditTextShopName.setText(priceList.getShopName());
                    mEditTextItemQty.setText(String.valueOf(priceList.getVariantPrice()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        editVariantList();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void editVariantList() {

        String shopName = mEditTextShopName.getText().toString();
        if (!shopName.equals("") && !mEditTextItemQty.getText().toString().isEmpty()){

            long price = Long.parseLong(mEditTextItemQty.getText().toString());
            PriceList priceList = new PriceList(shopName, price);
            priceReference.setValue(priceList);
        }else {
            Utils.showToast(getActivity(), getString(R.string.toast_fill_all_detail));
        }
    }
}

