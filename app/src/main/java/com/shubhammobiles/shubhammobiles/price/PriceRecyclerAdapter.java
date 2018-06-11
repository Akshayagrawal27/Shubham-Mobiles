package com.shubhammobiles.shubhammobiles.price;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.PriceList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Akshay on 23-02-2018.
 */

class PriceRecyclerAdapter extends FirebaseRecyclerAdapter<PriceList, PriceRecyclerAdapter.PriceRecyclerViewHolder> {

    static private PriceListClickListener mOnLongClickListener;
    long bestPrice = 0;

    String bestPriceKey;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
*                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param bestPriceKey
     */
    public PriceRecyclerAdapter(Class<PriceList> modelClass,
                                int modelLayout,
                                Class<PriceRecyclerViewHolder> viewHolderClass, Query ref,
                                PriceListClickListener mOnLongClickListener, String bestPriceKey) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mOnLongClickListener = mOnLongClickListener;
        this.bestPriceKey = bestPriceKey;
    }

    @Override
    protected void populateViewHolder(PriceRecyclerViewHolder viewHolder, PriceList priceList, int position) {

        viewHolder.tvShopName.setText(Utils.getNameToShow(priceList.getShopName()));
        viewHolder.tvVariantPrice.setText(String.format("₹ %s", String.valueOf(priceList.getVariantPrice())));

        /*if (!bestPriceKey.equals("") && bestPriceKey.equals(this.getRef(position).getKey())){
            viewHolder.tvVariantPrice.setTextColor(Color.parseColor("#FF4081"));
        }
*/
        HashMap<String, Object> timestampLastChanged = priceList.getTimeStampLastChanged();

        if (timestampLastChanged != null ){
            Long timestamp = (Long)timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
            try{
                Calendar calendar = Calendar.getInstance();
                TimeZone tz = TimeZone.getTimeZone(Constants.COUNTRY_TIMEZONE);
                calendar.setTimeInMillis(timestamp);
                calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
                Date currentTimeZone = (Date) calendar.getTime();
                viewHolder.tvPriceLastUpdate.setText("Last Updated on: " + sdf.format(currentTimeZone));
            }catch (Exception e) {

            }
        }
    }

    public interface PriceListClickListener {
        void onPriceListLongClick(int clickedItemIndex);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static public class PriceRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView tvShopName, tvVariantPrice, tvPriceLastUpdate;
        public PriceRecyclerViewHolder(View itemView) {
            super(itemView);

            tvShopName = (TextView) itemView.findViewById(R.id.tv_price_name);
            tvVariantPrice = (TextView) itemView.findViewById(R.id.tv_price_variant);
            tvPriceLastUpdate = (TextView) itemView.findViewById(R.id.tv_price_last_update);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            mOnLongClickListener.onPriceListLongClick(getAdapterPosition());
            return true;
        }
    }
}