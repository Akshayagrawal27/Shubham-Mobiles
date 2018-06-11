package com.shubhammobiles.shubhammobiles.Sharing;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.BrandList;
import com.shubhammobiles.shubhammobiles.model.User;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

/**
 * Created by Akshay on 21-02-2018.
 */

public class ShareStock extends Fragment implements ShareRecyclerAdapter.UserListClickListener {

    private static final String TAG = "MainActivity";
    private static View view;

    private EditText mSearchBox;
    private RecyclerView mRecyclerUserList;
    private ShareRecyclerAdapter mRecyclerUserListAdapter;

    private String mInput;
    private String brandKey;
    private String brandName;

    private Bundle brandBundle;
    private BrandList brandList;

    public ShareStock() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_share, container, false);


        brandBundle = getArguments();

        brandKey = brandBundle.getString(Constants.KEY_BRAND_KEY);
        brandName = brandBundle.getString(Constants.KEY_BRAND_NAME);

        mSearchBox = (EditText) view.findViewById(R.id.et_search_user);
        mRecyclerUserList = (RecyclerView) view.findViewById(R.id.recycler_share_with_user);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerUserList.setLayoutManager(layoutManager);

        mRecyclerUserListAdapter = new ShareRecyclerAdapter(User.class,
                R.layout.list_user,
                ShareRecyclerAdapter.ShareRecyclerViewHolder.class,
                FirebaseUtil.getSharedBrandWithReference(brandKey), ShareStock.this, brandKey,Constants.KEY_BRAND_SHARED);

        mRecyclerUserList.setAdapter(mRecyclerUserListAdapter);

        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                /* Get the input after every textChanged event and transform it to lowercase */
                mInput = mSearchBox.getText().toString().trim().toLowerCase();

                setupRecyclerAdapter();
            }
        });

        return view;
    }

    private void setupRecyclerAdapter() {
        /* Clean up the old adapter */
        if (mRecyclerUserListAdapter != null) mRecyclerUserListAdapter.cleanup();

        /* Nullify the adapter data if the input length is less than 2 characters */
        if (mInput.equals("") || mInput.length() < 2) {
            mRecyclerUserList.setAdapter(new ShareRecyclerAdapter(User.class,
                    R.layout.list_user,
                    ShareRecyclerAdapter.ShareRecyclerViewHolder.class,
                    FirebaseUtil.getSharedBrandWithReference(brandKey), ShareStock.this, brandKey, Constants.KEY_BRAND_SHARED));

            /* Define and set the adapter otherwise. */
        } else {
            mRecyclerUserListAdapter = new ShareRecyclerAdapter(User.class,
                    R.layout.list_user,
                    ShareRecyclerAdapter.ShareRecyclerViewHolder.class,
                    FirebaseUtil.getUserListReference().orderByChild(Constants.FIREBASE_PROPERTY_SHOP_NAME)
                            .startAt(mInput).endAt(mInput + "~"), ShareStock.this, brandKey,null);

            mRecyclerUserList.setAdapter(mRecyclerUserListAdapter);
        }
    }

    @Override
    public void onUserListItemClick(int clickedItemIndex) {

        buildAlert(mRecyclerUserListAdapter.getItem(clickedItemIndex), mRecyclerUserListAdapter.getRef(clickedItemIndex).getKey());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecyclerUserListAdapter != null)
            mRecyclerUserListAdapter.cleanup();
    }

    public void buildAlert(final User friend, final String friendKey) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomTheme_Dialog)
                .setTitle(getString(R.string.alert_share) + " " + brandName + " with " + friend.getShopName())
                .setMessage(getString(R.string.alert_are_u_sure_share_brand) + " " + friend.getShopName() + " ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUtil.shareBrand(true, friend, friendKey, brandKey, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                mSearchBox.setText(null);
                                Utils.showToast(getContext(), "Shared with " + friend.getShopName());
                                getActivity().finish();
                            }
                        });
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
}
