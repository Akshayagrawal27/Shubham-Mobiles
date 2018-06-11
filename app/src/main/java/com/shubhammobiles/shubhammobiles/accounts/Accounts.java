package com.shubhammobiles.shubhammobiles.accounts;

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
import com.shubhammobiles.shubhammobiles.model.AccountList;
import com.shubhammobiles.shubhammobiles.notelist.NoteListActivity;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

/**
 * Created by Akshay on 09-03-2018.
 */

public class Accounts extends Fragment implements AccountsRecyclerAdapter.AccountListClickListener{

    View view;

    private DatabaseReference accountListReference, noteListReference;

    private RecyclerView mRecyclerAccountList;
    private AccountsRecyclerAdapter mAccountRecyclerAdapter;

    private boolean mOwner = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.nav_accounts, container, false);

        accountListReference = FirebaseUtil.getAccountListReference();
        noteListReference = FirebaseUtil.getNoteListReference();

        mRecyclerAccountList = (RecyclerView) view.findViewById(R.id.recycler_accounts_list);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerAccountList.setLayoutManager(layoutManager);

        mAccountRecyclerAdapter = new AccountsRecyclerAdapter(AccountList.class,
                R.layout.list_accounts,
                AccountsRecyclerAdapter.AccountsRecyclerViewHolder.class,
                accountListReference, this, getContext());

        mRecyclerAccountList.setAdapter(mAccountRecyclerAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                String owner = mAccountRecyclerAdapter.getItem(viewHolder.getLayoutPosition()).getOwner();

                if (Utils.checkOwner(owner)) {
                    if (swipeDir == ItemTouchHelper.LEFT){
                        String accountId = mAccountRecyclerAdapter.getRef(viewHolder.getLayoutPosition()).getKey();
                        String accountName = mAccountRecyclerAdapter.getItem(viewHolder.getLayoutPosition()).getAccountName();
                        buildAlert(viewHolder, accountId, accountName);
                    }
                } else {
                    Utils.showToast(getContext(), "You are not the owner");
                    mAccountRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            }
        }).attachToRecyclerView(mRecyclerAccountList);

        return view;
    }

    @Override
    public void onAccountListItemClick(int clickedItemIndex) {
        mOwner = Utils.checkOwner(mAccountRecyclerAdapter.getItem(clickedItemIndex).getOwner());
        Intent intent = new Intent(getContext(), NoteListActivity.class);
        intent.putExtra(Constants.KEY_ACCOUNT_NAME, mAccountRecyclerAdapter.getItem(clickedItemIndex).getAccountName());
        intent.putExtra(Constants.KEY_ACCOUNT_KEY, mAccountRecyclerAdapter.getRef(clickedItemIndex).getKey());
        intent.putExtra(Constants.KEY_AM_I_OWNER, mOwner);
        startActivity(intent);
    }

    private void removeItem(String accountId) {
        accountListReference.child(accountId).removeValue();
        noteListReference.child(accountId).removeValue();
    }

    public void buildAlert(final RecyclerView.ViewHolder viewHolder, final String accountId, String accountName){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomTheme_Dialog)
                .setTitle(getString(R.string.alert_remove) + " " + accountName)
                .setMessage(getString(R.string.alert_are_u_sure_remove_msg) + " " + accountName + " ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(accountId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAccountRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
