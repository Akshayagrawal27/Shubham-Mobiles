package com.shubhammobiles.shubhammobiles.price;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.BrandModelList;


/**
 * Created by Akshay on 19-03-2018.
 */

class CopyPasteBrandModelRecyclerAdapter extends FirebaseRecyclerAdapter<BrandModelList, CopyPasteBrandModelRecyclerAdapter.CopyPasteBrandModelRecyclerViewHolder> {

    static private CopyPasteBrandModelClickListener mOnCopyPasteBrandModelClickListener;

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
    public CopyPasteBrandModelRecyclerAdapter(Class<BrandModelList> modelClass,
                                              int modelLayout,
                                              Class<CopyPasteBrandModelRecyclerViewHolder> viewHolderClass, Query ref,
                                              CopyPasteBrandModelClickListener mOnCopyPasteBrandModelClickListener) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mOnCopyPasteBrandModelClickListener = mOnCopyPasteBrandModelClickListener;
    }

    @Override
    protected void populateViewHolder(CopyPasteBrandModelRecyclerViewHolder viewHolder, BrandModelList brandModelList, int position) {

        viewHolder.tvCopyPaste.setText(brandModelList.getBrandModelName());
    }

    public interface CopyPasteBrandModelClickListener {
        void onCopyPasteBrandModelClick(int clickedItemIndex);
    }

    static public class CopyPasteBrandModelRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvCopyPaste;
        public CopyPasteBrandModelRecyclerViewHolder(View itemView) {
            super(itemView);
            tvCopyPaste = (TextView) itemView.findViewById(R.id.tv_copy_paste);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnCopyPasteBrandModelClickListener.onCopyPasteBrandModelClick(getAdapterPosition());
        }
    }
}