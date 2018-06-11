package com.shubhammobiles.shubhammobiles.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.shubhammobiles.shubhammobiles.BroadcastReceiver.AlarmBroadcastReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class OrderReminderUtil {

    private static Calendar cal;

    public static void setReminder(Context context, String deliveryDate){

        int year = getYear(deliveryDate);
        int month = getMonth(deliveryDate);
        int day = getDay(deliveryDate);

        setCalendar(year, month-1, day);

        Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 1253, alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager1 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager1.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent1);
    }

    private static void setCalendar(int year, int month, int day) {
        cal=Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        cal.set(year,month,day,9,00);
    }

    public static int getYear(String date){
        return Integer.parseInt(date.substring(0, 4));
    }

    public static int getMonth(String date){
        return Integer.parseInt(date.substring(5, 7));
    }

    public static int getDay(String date){
        return Integer.parseInt(date.substring(8,10));
    }
}
