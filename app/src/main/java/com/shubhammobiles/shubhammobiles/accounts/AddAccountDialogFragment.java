package com.shubhammobiles.shubhammobiles.accounts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.AccountList;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

public class AddAccountDialogFragment extends DialogFragment {
    EditText etAccountName, etAccountAddress, etAccountContactNumber;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddAccountDialogFragment newInstance() {
        AddAccountDialogFragment addListDialogFragment = new AddAccountDialogFragment();
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
        View rootView = inflater.inflate(R.layout.dialog_add_account_list, null);

        etAccountName = (EditText) rootView.findViewById(R.id.et_accounts_name);
        etAccountAddress = (EditText) rootView.findViewById(R.id.et_accounts_address);
        etAccountContactNumber = (EditText) rootView.findViewById(R.id.et_accounts_contact_number);

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addAccountList();
                    }
                });

        return builder.create();
    }

    /**
     * Add new account list
     */
    public void addAccountList() {
        String accountName = etAccountName.getText().toString().toLowerCase();
        String accountAddress = etAccountAddress.getText().toString().toLowerCase();
        String accountContactNumber = etAccountContactNumber.getText().toString();
        String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference accountReference;

        if (checkData(accountName, accountAddress, accountContactNumber)){
            accountReference = FirebaseUtil.getAccountListReference();
            accountReference.child(accountReference.push().getKey())
                    .setValue(new AccountList(accountName, accountAddress, Utils.formattedPhoneNumber(accountContactNumber), owner));
        }
    }

    private boolean checkData(String accountName, String accountAddress, String accountContactNumber) {

        boolean allRight = true;

        if (accountName.equals("")){
            etAccountName.setError(getResources().getString(R.string.error_cannot_be_empty));
            allRight = false;
        }
        if (accountAddress.equals("")){
            etAccountAddress.setError(getResources().getString(R.string.error_cannot_be_empty));
            allRight = false;
        }
        if (accountContactNumber.equals("") || !Utils.isPhoneNumberValid(accountContactNumber)){
            etAccountContactNumber.setError(getString(R.string.error_invalid_number));
            allRight = false;
        }
        return allRight;
    }
}

