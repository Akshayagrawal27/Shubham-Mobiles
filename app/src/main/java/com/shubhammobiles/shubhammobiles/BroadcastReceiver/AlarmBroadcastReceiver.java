package com.shubhammobiles.shubhammobiles.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shubhammobiles.shubhammobiles.util.NotificationUtils;


/**
 * Created by Akshay on 27-04-2018.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.remindUserOfAdvanceDeliveryOrder(context);
    }
}
