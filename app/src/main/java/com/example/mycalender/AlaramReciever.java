package com.example.mycalender;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.TimeZone;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlaramReciever extends BroadcastReceiver {


    private NotificationManager mNotificationManager;
    private static String PRIMARY_CHANNEL_ID = "primary_chnl_id";
    private static int NOTIFICATION_ID =0;

    @Override
    public void onReceive(Context context, Intent notifyintent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        deliverNotification(context);
    }

    private void deliverNotification(Context context) {
        // Create the content intent for the notification, which launches
        // this activity

        Context appContext = context.getApplicationContext();
        Resources res = appContext.getResources();

        CalcPanchang calc = CalcPanchang.getInstance(appContext);
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int tzOffset =  c.get(Calendar.ZONE_OFFSET);
        String str[] = calc.getPanchangNotify(year,month,day,tzOffset);

        Intent contentIntent = new Intent(context, MainActivity.class);

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent
                        .FLAG_UPDATE_CURRENT);
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti_panchang)
                .setContentTitle(str[0])
                .setContentText(str[1])
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Deliver the notification
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
