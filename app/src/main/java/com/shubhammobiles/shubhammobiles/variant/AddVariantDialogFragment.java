package com.shubhammobiles.shubhammobiles.variant;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.VariantList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

public class AddVariantDialogFragment extends DialogFragment {
    String brandKey,brandModelKey;
    EditText mEditTextListName, mEditTextItemQty;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddVariantDialogFragment newInstance(String brandKey, String brandModelKey) {
        AddVariantDialogFragment addListDialogFragment = new AddVariantDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_BRAND_KEY, brandKey);
        bundle.putString(Constants.KEY_BRAND_MODEL_KEY, brandModelKey);
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
}

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View rootView = inflater.inflate(R.layout.dialog_add_list, null);
        mEditTextListName = (EditText) rootView.findViewById(R.id.edit_text_list_name);
        mEditTextItemQty = (EditText) rootView.findViewById(R.id.edit_text_list_qty);

        mEditTextListName.setHint(getResources().getString(R.string.hint_add_variant_name));
        mEditTextItemQty.setVisibility(View.VISIBLE);

        /**
         * Call addVariantList() when user taps "Done" keyboard action
         */
        mEditTextListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addVariantList();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addVariantList();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void addVariantList() {
        String stockName = mEditTextListName.getText().toString();
        DatabaseReference variantReference;

        if (!stockName.equals("")) {
            if(!mEditTextItemQty.getText().toString().isEmpty()) {
                int variantItemQty = Integer.parseInt(mEditTextItemQty.getText().toString());
                variantReference = FirebaseUtil.getModelVariantListReference().child(brandKey).child(brandModelKey);
                variantReference.child(variantReference.push().getKey()).setValue(new VariantList(stockName, variantItemQty, null));

                Utils.updateCurrentTimeStamp(brandKey);
            }else
                mEditTextItemQty.setError(getResources().getString(R.string.error_cannot_be_empty));
        }else
            mEditTextListName.setError(getResources().getString(R.string.error_cannot_be_empty));
    }
}

