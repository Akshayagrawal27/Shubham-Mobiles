package com.shubhammobiles.shubhammobiles.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.shubhammobiles.shubhammobiles.MainActivity;
import com.shubhammobiles.shubhammobiles.R;

public class NotificationUtils {

    private static final int ADVANCE_ORDER_NOTIFICATION_ID = 1127;
    private static final int ADVANCE_ORDER_REMINDER_PENDING_INTENT_ID = 3417;
    private static final String ADVANCE_ORDER_NOTIFICATION_CHANNEL_ID = "advance order reminder channel";

    public static void remindUserOfAdvanceDeliveryOrder(Context context){

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(
                    ADVANCE_ORDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ADVANCE_ORDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.shubhammobiles)
                .setLargeIcon(largeIcon(context))
                .setContentTitle("Delivery Due")
                .setContentText("You need to deliver it today")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        "You need to deliver it today"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(ADVANCE_ORDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context) {

        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                ADVANCE_ORDER_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {

        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.shubhammobiles);
        return largeIcon;
    }
}
