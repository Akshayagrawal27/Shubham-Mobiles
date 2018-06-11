package com.shubhammobiles.shubhammobiles.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.model.OrderList;
import com.shubhammobiles.shubhammobiles.util.Constants;
import com.shubhammobiles.shubhammobiles.util.FirebaseUtil;
import com.shubhammobiles.shubhammobiles.util.Utils;

/**
 * Created by Akshay on 04-03-2018.
 */

public class Order extends Fragment implements OrderRecyclerAdapter.OrderListClickListener{

    private static final String TAG = "OrderFragment";
    private static View view;

    private Query orderListQuery;
    private DatabaseReference orderListReference;

    private RecyclerView mRecyclerOrderList;
    private OrderRecyclerAdapter mOrderRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nav_order, container, false);

        orderListQuery = FirebaseUtil.getOrderListReference().orderByChild(Constants.FIREBASE_PROPERTY_BOOKING_DATE);
        orderListReference = FirebaseUtil.getOrderListReference();

        mRecyclerOrderList = (RecyclerView) view.findViewById(R.id.recycler_order);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);

        mRecyclerOrderList.setLayoutManager(layoutManager);

        mOrderRecyclerAdapter = new OrderRecyclerAdapter(OrderList.class,
                R.layout.list_order,
                OrderRecyclerAdapter.OrderRecyclerViewHolder.class,
                orderListQuery, this);

        mRecyclerOrderList.setAdapter(mOrderRecyclerAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.LEFT) {
                    String orderId = mOrderRecyclerAdapter.getRef(viewHolder.getLayoutPosition()).getKey();
                    String orderNumber = mOrderRecyclerAdapter.getItem(viewHolder.getLayoutPosition()).getBillNumber();
                    buildAlert(viewHolder, orderId, orderNumber);
                }
            }
        }).attachToRecyclerView(mRecyclerOrderList);

        return view;
    }

    @Override
    public void onOrderListItemClick(int clickedItemIndex) {
        Toast.makeText(getContext(), mOrderRecyclerAdapter.getItem(clickedItemIndex).getCustomerName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), OrderDetail.class);
        intent.putExtra(Constants.KEY_ORDER, mOrderRecyclerAdapter.getRef(clickedItemIndex).getKey());
        startActivity(intent);
    }

    @Override
    public void onOrderListItemLongClick(int clickedItemIndex) {
        Utils.showToast(getContext(), mOrderRecyclerAdapter.getItem(clickedItemIndex).getModelName());
        Intent intent = new Intent(getContext(), AddOrder.class);
        intent.setAction(Constants.EDIT_ORDER);
        intent.putExtra(Constants.KEY_ORDER, mOrderRecyclerAdapter.getRef(clickedItemIndex).getKey());
        startActivity(intent);
    }

    public void buildAlert(final RecyclerView.ViewHolder viewHolder, final String id, String orderNumber) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomTheme_Dialog)
                .setTitle(getString(R.string.alert_remove_order) + orderNumber)
                .setMessage(getString(R.string.alert_are_u_sure_remove_order))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(id);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mOrderRecyclerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void removeItem(String brandModelId) {
        orderListReference.child(brandModelId).removeValue();
    }
}
