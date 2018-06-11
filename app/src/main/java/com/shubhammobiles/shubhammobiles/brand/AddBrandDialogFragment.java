package com.shubhammobiles.shubhammobiles.brand;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.BrandList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;

import java.util.HashMap;

public class AddBrandDialogFragment extends DialogFragment {
    EditText mEditTextListName, mEditTextItemQty;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddBrandDialogFragment newInstance() {
        AddBrandDialogFragment addListDialogFragment = new AddBrandDialogFragment();
        Bundle bundle = new Bundle();
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
}

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        mEditTextListName.setHint(getResources().getString(R.string.hint_add_brand_name));

        /**
         * Call addBrandList() when user taps "Done" keyboard action
         */
        mEditTextListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addBrandList();
                    getDialog().dismiss();
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
                        addBrandList();
                    }
                });

        return builder.create();
    }

    /**
     * Add new brand list
     */
    public void addBrandList() {
        String brandName = mEditTextListName.getText().toString();
        String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference brandReference;

        if (!brandName.equals("")) {
            brandReference = FirebaseUtil.getBrandListReference();
            HashMap<String, Object> timestampCreated = new HashMap<>();
            timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
            brandReference
                    .child(brandReference.push().getKey())
                    .setValue(new BrandList(brandName, 0, owner, timestampCreated));
        }else{
            mEditTextListName.setError(getResources().getString(R.string.error_cannot_be_empty));
        }
    }
}

