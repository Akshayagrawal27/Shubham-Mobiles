package com.shubhammobiles.shubhammobiles.order;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.OrderList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.Utils;

/**
 * Created by Akshay on 04-03-2018.
 */

public class OrderRecyclerAdapter extends FirebaseRecyclerAdapter<OrderList, OrderRecyclerAdapter.OrderRecyclerViewHolder> {

    static private OrderListClickListener mOnClickListener, mOnLongClickListener;

    /**
     * @param orderListClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
*                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param mOnClickListener
     *
     */
    public OrderRecyclerAdapter(Class<OrderList> orderListClass, int modelLayout,
                                Class<OrderRecyclerViewHolder> viewHolderClass, Query ref, OrderListClickListener mOnClickListener) {
        super(orderListClass, modelLayout, viewHolderClass, ref);
        this.mOnClickListener = mOnClickListener;
        this.mOnLongClickListener = mOnClickListener;
    }

    @Override
    protected void populateViewHolder(OrderRecyclerViewHolder viewHolder, OrderList orderList, int position) {

        viewHolder.tvDate.setText(Utils.getDateToShowFromFirebase(orderList.getBookingDate()));

        if (orderList.getCustomerName().length() > 17)
            viewHolder.tvCustomerName.setText(Utils.getNameToShow(orderList.getCustomerName()).substring(0, 18) + "...");
        else
            viewHolder.tvCustomerName.setText(Utils.getNameToShow(orderList.getCustomerName()));

        viewHolder.tvCustomerAddress.setText(Utils.getNameToShow(orderList.getCustomerAddress()));
        viewHolder.tvModelName.setText(Utils.getNameToShow(orderList.getModelName()));
        viewHolder.tvDeliveryDate.setText(Utils.getDateToShowFromFirebase(orderList.getDeliveryDate()));

        if (orderList.getStatus() != null && orderList.getStatus().equals(Constants.ORDER_STATUS_PENDING)) {
            viewHolder.clListOrder.setBackgroundColor(Color.parseColor("#effff1"));
        }
    }

    public interface OrderListClickListener {
        void onOrderListItemClick(int clickedItemIndex);
        void onOrderListItemLongClick(int clickedItemIndex);
    }

    public static class OrderRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView tvDate, tvCustomerName, tvCustomerAddress, tvModelName, tvDeliveryDate;
        ConstraintLayout clListOrder;

        public OrderRecyclerViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.tv_booking_date);
            tvCustomerName = (TextView) itemView.findViewById(R.id.tv_customer_name);
            tvCustomerAddress = (TextView) itemView.findViewById(R.id.tv_customer_address);
            tvModelName = (TextView) itemView.findViewById(R.id.tv_model_name);
            tvDeliveryDate = (TextView) itemView.findViewById(R.id.tv_delivery_date);
            clListOrder = (ConstraintLayout) itemView.findViewById(R.id.list_order);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onOrderListItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mOnLongClickListener.onOrderListItemLongClick(getAdapterPosition());
            return true;
        }
    }
}
