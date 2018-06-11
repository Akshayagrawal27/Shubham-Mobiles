package com.shubhammobiles.shubhammobiles.brandmodel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.BrandModelList;

/**
 * Created by Akshay on 23-02-2018.
 */

class BrandModelRecyclerAdapter extends FirebaseRecyclerAdapter<BrandModelList, BrandModelRecyclerAdapter.BrandModelRecyclerViewHolder> {

    static private BrandModelListClickListener mOnClickListener;

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
    public BrandModelRecyclerAdapter(Class<BrandModelList> modelClass,
                                     int modelLayout,
                                     Class<BrandModelRecyclerViewHolder> viewHolderClass, Query ref,
                                     BrandModelListClickListener mOnClickListener) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    protected void populateViewHolder(BrandModelRecyclerViewHolder viewHolder, BrandModelList brandModelList, int position) {

        viewHolder.tvBrandModelName.setText(brandModelList.getBrandModelName());
        viewHolder.tvBrandModelQty.setText("Total: " + brandModelList.getBrandModelQty());
    }

    public interface BrandModelListClickListener {
        void onBrandModelListItemClick(int clickedItemIndex);
    }

    static public class BrandModelRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvBrandModelName, tvBrandModelQty;
        public BrandModelRecyclerViewHolder(View itemView) {
            super(itemView);

            tvBrandModelName = (TextView) itemView.findViewById(R.id.tv_brand_model_name);
            tvBrandModelQty = (TextView) itemView.findViewById(R.id.tv_brand_model_qty);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onBrandModelListItemClick(getAdapterPosition());
        }
    }
}
