package com.shubhammobiles.shubhammobiles.price;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.VariantList;

/**
 * Created by Akshay on 19-03-2018.
 */

class CopyPasteVariantRecyclerAdapter extends FirebaseRecyclerAdapter<VariantList, CopyPasteVariantRecyclerAdapter.CopyPasteVariantRecyclerViewHolder> {

    static private CopyPasteVariantClickListener mOnCopyPasteVariantClickListener;

    String variantKey;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
*                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param variantKey
     */
    public CopyPasteVariantRecyclerAdapter(Class<VariantList> modelClass,
                                           int modelLayout,
                                           Class<CopyPasteVariantRecyclerViewHolder> viewHolderClass, Query ref,
                                           CopyPasteVariantClickListener mOnCopyPasteVariantClickListener, String variantKey) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mOnCopyPasteVariantClickListener = mOnCopyPasteVariantClickListener;
        this.variantKey = variantKey;
    }

    @Override
    protected void populateViewHolder(CopyPasteVariantRecyclerViewHolder viewHolder, VariantList variantList, int position) {

        if (variantKey != null && !this.getRef(position).getKey().equals(variantKey))
            viewHolder.tvCopyPaste.setText(variantList.getVariantName());
        else {
            viewHolder.itemView.setVisibility(View.GONE);
            viewHolder.itemView.getLayoutParams().height = 0;
        }
    }

    public interface CopyPasteVariantClickListener {
        void onCopyPasteVariantClick(int clickedItemIndex);
    }

    static public class CopyPasteVariantRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvCopyPaste;
        public CopyPasteVariantRecyclerViewHolder(View itemView) {
            super(itemView);
            tvCopyPaste = (TextView) itemView.findViewById(R.id.tv_copy_paste);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnCopyPasteVariantClickListener.onCopyPasteVariantClick(getAdapterPosition());
        }
    }
}