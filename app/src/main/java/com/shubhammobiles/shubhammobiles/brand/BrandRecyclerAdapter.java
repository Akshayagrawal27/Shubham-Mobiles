package com.shubhammobiles.shubhammobiles.brand;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.BrandList;
import com.shubhammobiles.shubhammobiles.model.User;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Akshay on 23-02-2018.
 */

class BrandRecyclerAdapter extends FirebaseRecyclerAdapter<BrandList, BrandRecyclerAdapter.BrandRecyclerViewHolder> {

    static private BrandListClickListener mOnClickListener;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public BrandRecyclerAdapter(Class<BrandList> modelClass,
                                int modelLayout,
                                Class<BrandRecyclerViewHolder> viewHolderClass, Query ref,
                                BrandListClickListener mOnClickListener) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    protected void populateViewHolder(final BrandRecyclerViewHolder viewHolder, final BrandList brandList, int position) {
        viewHolder.tvBrandName.setText(brandList.getBrandName());
        viewHolder.tvBrandQty.setText("Total: " + brandList.getBrandQuantity());

        if (!Utils.checkOwner(brandList.getOwner())){
            viewHolder.clStockList.setBackgroundColor(Color.parseColor("#effff1"));
            viewHolder.tvBrandSharedBy.setVisibility(View.VISIBLE);

            FirebaseUtil.getUserListReference().child(brandList.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null){
                        User user = dataSnapshot.getValue(User.class);
                        viewHolder.tvBrandSharedBy.setText("Shared By: " + user.getShopName());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        HashMap<String, Object> timestampLastChanged = brandList.getTimestampLastChanged();

        if (timestampLastChanged != null ){
            Long timestamp = (Long)timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
            try{
                Calendar calendar = Calendar.getInstance();
                TimeZone tz = TimeZone.getTimeZone(Constants.COUNTRY_TIMEZONE);
                calendar.setTimeInMillis(timestamp);
                calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
                Date currentTimeZone = (Date) calendar.getTime();
                viewHolder.tvBrandLastUpdate.setText("Last Updated On: " + sdf.format(currentTimeZone));
            }catch (Exception e) {

            }
        }
    }

    public interface BrandListClickListener {
        void onBrandListItemClick(int clickedItemIndex);
    }

    static public class BrandRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvBrandName, tvBrandQty, tvBrandLastUpdate, tvBrandSharedBy;
        ConstraintLayout clStockList;
        public BrandRecyclerViewHolder(View itemView) {
            super(itemView);

            tvBrandName = (TextView) itemView.findViewById(R.id.tv_brand_name);
            tvBrandQty = (TextView) itemView.findViewById(R.id.tv_brand_qty);
            tvBrandLastUpdate = (TextView) itemView.findViewById(R.id.tv_brand_last_update);
            tvBrandSharedBy = (TextView) itemView.findViewById(R.id.tv_brand_shared_by);
            clStockList = (ConstraintLayout) itemView.findViewById(R.id.stock_list);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onBrandListItemClick(getAdapterPosition());
        }
    }
}