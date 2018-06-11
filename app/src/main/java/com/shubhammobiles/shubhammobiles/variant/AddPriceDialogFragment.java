package com.shubhammobiles.shubhammobiles.variant;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.PriceList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

import java.util.ArrayList;

public class AddPriceDialogFragment extends DialogFragment {
    EditText mEditTextItemQty, mEditTextShopName;
    Spinner spinner;

    String variantKey, brandKey, brandModelKey, variantName;

    public ArrayList<String> variantNameList = new ArrayList<>();
    public ArrayList<String> variantKeyList = new ArrayList<>();

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     * @param variantNameList
     * @param variantKey
     * @param brandKey
     * @param brandModelKey
     */
    public static AddPriceDialogFragment newInstance(ArrayList<String> variantNameList, ArrayList<String> variantKey,
                                                     String brandKey, String brandModelKey) {
        AddPriceDialogFragment addListDialogFragment = new AddPriceDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.KEY_VARIANT_NAME, variantNameList);
        bundle.putStringArrayList(Constants.KEY_VARIANT_KEY, variantKey);
        bundle.putString(Constants.KEY_BRAND_MODEL_KEY, brandModelKey);
        bundle.putString(Constants.KEY_BRAND_KEY, brandKey);
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
}

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        variantNameList = getArguments().getStringArrayList(Constants.KEY_VARIANT_NAME);
        variantKeyList = getArguments().getStringArrayList(Constants.KEY_VARIANT_KEY);
        brandKey = getArguments().getString(Constants.KEY_BRAND_KEY);
        brandModelKey = getArguments().getString(Constants.KEY_BRAND_MODEL_KEY);
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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_price, null);

        mEditTextItemQty = (EditText) rootView.findViewById(R.id.edit_text_list_qty);
        mEditTextShopName = (EditText) rootView.findViewById(R.id.edit_text_list_shop_name);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);

        mEditTextShopName.setVisibility(View.VISIBLE);
        mEditTextItemQty.setVisibility(View.VISIBLE);
        mEditTextItemQty.setHint(getResources().getString(R.string.hint_add_price));


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                variantKey = variantKeyList.get(i);
                variantName = variantNameList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, variantNameList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addPriceList();
                    }
                });

        return builder.create();
    }

    /**
     * Add new price list
     */
    public void addPriceList() {

        String shopName = mEditTextShopName.getText().toString().toLowerCase();
        if (!shopName.equals("") && !mEditTextItemQty.getText().toString().isEmpty()){
            long price = Long.parseLong(mEditTextItemQty.getText().toString());

            DatabaseReference priceReference = FirebaseUtil.getPriceListReference().child(variantKey);
            PriceList priceList = new PriceList(shopName, price);

            priceReference.child(priceReference.push().getKey()).setValue(priceList);
        }else {
            Utils.showToast(getActivity(), getString(R.string.toast_fill_all_detail));
        }
    }
}

