package com.shubhammobiles.shubhammobiles.Sharing;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.User;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

/**
 * Created by Akshay on 23-02-2018.
 */

class ShareRecyclerAdapter extends FirebaseRecyclerAdapter<User, ShareRecyclerAdapter.ShareRecyclerViewHolder> {

    static private UserListClickListener mOnClickListener;
    final String KEY_SHARED;
    String key;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
*                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param key
     * @param keyShared
     */
    public ShareRecyclerAdapter(Class<User> modelClass,
                                int modelLayout,
                                Class<ShareRecyclerViewHolder> viewHolderClass, Query ref,
                                UserListClickListener mOnClickListener, String key, String keyShared) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mOnClickListener = mOnClickListener;
        this.key = key;
        this.KEY_SHARED = keyShared;
    }

    @Override
    protected void populateViewHolder(final ShareRecyclerViewHolder viewHolder, final User user, final int position) {

        viewHolder.tvUserList.setText(showName(user.getShopName()));
        viewHolder.tvPhoneNumber.setText(user.getPhoneNumber());

        if (KEY_SHARED != null && KEY_SHARED.equals(Constants.KEY_BRAND_SHARED)){
            viewHolder.ivShareCheck.setImageResource(R.drawable.sharp_check_circle_black_24);

            viewHolder.ivShareCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseUtil.shareBrand(false, user, getRef(position).getKey(), key, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                        }
                    });
                }
            });
        }

        if (KEY_SHARED != null && KEY_SHARED.equals(Constants.KEY_ACCOUNT_SHARED)){
            viewHolder.ivShareCheck.setImageResource(R.drawable.sharp_check_circle_black_24);

            viewHolder.ivShareCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseUtil.shareAccount(false, user, getRef(position).getKey(), key, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                        }
                    });
                }
            });
        }

    }

    private String showName(String shopName) {
        return Utils.getNameToShow(shopName);
    }

    public interface UserListClickListener {
        void onUserListItemClick(int clickedItemIndex);
    }

    static public class ShareRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvUserList, tvPhoneNumber;
        ImageView ivShareCheck;

        public ShareRecyclerViewHolder(View itemView) {
            super(itemView);

            tvUserList = (TextView) itemView.findViewById(R.id.tv_share_shop_name);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tv_share_phone_number);
            ivShareCheck = (ImageView) itemView.findViewById(R.id.iv_share_check);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onUserListItemClick(getAdapterPosition());
        }
    }
}