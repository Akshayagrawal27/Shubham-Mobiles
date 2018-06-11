package com.shubhammobiles.shubhammobiles.variant;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.VariantList;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

/**
 * Created by Akshay on 23-02-2018.
 */

class VariantRecyclerAdapter extends FirebaseRecyclerAdapter<VariantList, VariantRecyclerAdapter.VariantItemRecyclerViewHolder> {

    private DatabaseReference variantReference, brandReference, priceListReference;
    private ValueEventListener priceReferenceListener;
    String editedBy, brandKey;
    String variantKey;

    static private VariantListClickListener mOnClickListener;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param brandKey
     */
    public VariantRecyclerAdapter(Class<VariantList> modelClass,
                                  int modelLayout,
                                  Class<VariantItemRecyclerViewHolder> viewHolderClass, Query ref, String brandKey,
                                  VariantListClickListener mOnClickListener) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.variantReference = (DatabaseReference) ref;
        this.editedBy = null;
        this.brandKey = brandKey;
        brandReference = FirebaseUtil.getBrandListReference().child(brandKey);
        priceListReference = FirebaseUtil.getPriceListReference();
        this.mOnClickListener = mOnClickListener;
    }

    public interface VariantListClickListener {
        void onVariantListItemClick(int clickedItemIndex);
    }

    @Override
    protected void populateViewHolder(final VariantItemRecyclerViewHolder viewHolder, final VariantList variantList, final int position) {

        viewHolder.tvItemName.setText(variantList.getVariantName());
        viewHolder.tvItemQty.setText("Qty: " + variantList.getVariantQuantity());

        variantKey = this.getRef(position).getKey();

        if (variantList.getVariantBestPrice() != 0) {
            Log.wtf("priceActivity", "VariantRecycler: not zero");
            viewHolder.tvBestPrice.setText(String.format("₹ %s", String.valueOf(variantList.getVariantBestPrice())));
        } else{
            Log.wtf("priceActivity", "VariantRecycler: zero");
            viewHolder.tvBestPrice.setVisibility(View.GONE);
        }

        final String variantKey = this.getRef(position).getKey();
        final String editedBy = this.editedBy;

        viewHolder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = variantList.getVariantName();
                int qty = variantList.getVariantQuantity();
                ++qty;
                variantReference.child(variantKey).setValue(new VariantList(name, qty, editedBy));
                Utils.updateCurrentTimeStamp(brandKey);
            }
        });

        viewHolder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = variantList.getVariantName();
                int qty = variantList.getVariantQuantity();
                if (qty > 0) {
                    --qty;
                    variantReference.child(variantKey).setValue(new VariantList(name, qty, editedBy));
                    Utils.updateCurrentTimeStamp(brandKey);
                } else {
                    //Toast.makeText(VariantActivity.class, "Cannot be less than 0", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(VariantItemRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public void onViewDetachedFromWindow(VariantItemRecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (priceReferenceListener != null)
            priceListReference.removeEventListener(priceReferenceListener);
    }

    static public class VariantItemRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvItemName, tvItemQty;
        TextView tvAdd, tvMinus, tvBestPrice;

        public VariantItemRecyclerViewHolder(View itemView) {
            super(itemView);

            tvItemName = (TextView) itemView.findViewById(R.id.text_view_active_stock_item_name);
            tvItemQty = (TextView) itemView.findViewById(R.id.text_view_stock_item_qty);
            tvAdd = (TextView) itemView.findViewById(R.id.tv_add_item);
            tvMinus = (TextView) itemView.findViewById(R.id.tv_minus_item);
            tvBestPrice = (TextView) itemView.findViewById(R.id.tv_variant_best_price);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onVariantListItemClick(getAdapterPosition());
        }
    }
}
