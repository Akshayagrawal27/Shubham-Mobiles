package com.shubhammobiles.shubhammobiles.accounts;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.AccountList;
import com.shubhammobiles.shubhammobiles.model.User;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

/**
 * Created by Akshay on 10-03-2018.
 */

class AccountsRecyclerAdapter extends FirebaseRecyclerAdapter<AccountList, AccountsRecyclerAdapter.AccountsRecyclerViewHolder> {

    static private AccountListClickListener mOnClickListener;
    private static final int REQUEST_CALL_PHONE = 1;
    private final Context context;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
*                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param context
     */
    public AccountsRecyclerAdapter(Class<AccountList> modelClass,
                                   int modelLayout,
                                   Class<AccountsRecyclerViewHolder> viewHolderClass, Query ref,
                                   AccountListClickListener mOnClickListener, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mOnClickListener = mOnClickListener;
        this.context = context;
    }

    @Override
    protected void populateViewHolder(final AccountsRecyclerViewHolder viewHolder, final AccountList accountList, int position) {

        viewHolder.tvAccountName.setText(Utils.getNameToShow(accountList.getAccountName()));
        viewHolder.tvAccountAddress.setText(Utils.getNameToShow(accountList.getAccountAddress()));
        viewHolder.tvAccountContactNumber.setText(accountList.getContactNumber());

        viewHolder.tvAccountContactNumber.setPaintFlags(viewHolder.tvAccountContactNumber.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);

        viewHolder.tvAccountContactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    Utils.callToThisNumber(context, accountList.getContactNumber());
                else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                }
            }
        });

        if (!Utils.checkOwner(accountList.getOwner())){
            viewHolder.clAccountList.setBackgroundColor(Color.parseColor("#effff1"));
            viewHolder.tvAccountSharedBy.setVisibility(View.VISIBLE);

            FirebaseUtil.getUserListReference().child(accountList.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null){
                        User user = dataSnapshot.getValue(User.class);
                        viewHolder.tvAccountSharedBy.setText("Shared By: " + user.getShopName());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public interface AccountListClickListener {
        void onAccountListItemClick(int clickedItemIndex);
    }

    static public class AccountsRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAccountName, tvAccountContactNumber, tvAccountAddress, tvAccountSharedBy;
        ConstraintLayout clAccountList;

        public AccountsRecyclerViewHolder(View itemView) {
            super(itemView);

            tvAccountName = (TextView) itemView.findViewById(R.id.tv_account_name);
            tvAccountContactNumber = (TextView) itemView.findViewById(R.id.tv_account_contact_number);
            tvAccountAddress = (TextView) itemView.findViewById(R.id.tv_account_address);
            tvAccountSharedBy = (TextView) itemView.findViewById(R.id.tv_account_shared_by);
            clAccountList = (ConstraintLayout) itemView.findViewById(R.id.accounts_list);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onAccountListItemClick(getAdapterPosition());
        }
    }
}