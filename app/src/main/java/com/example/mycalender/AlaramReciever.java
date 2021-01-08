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
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlaramReciever extends BroadcastReceiver {


    private NotificationManager mNotificationManager;
    private static final String PRIMARY_CHANNEL_ID = "panchang_notification_channel";
    private static int NOTIFICATION_ID =0;
    TextToSpeech tts;
    @Override
    public void onReceive(Context context, Intent notifyintent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        Log.d("key","alaram");
        deliverstandupNotification(context);
    }

    private void deliverNotification(Context context) {
        // Create the content intent for the notification, which launches
        // this activity
 String str = "pamchang_notify";
       // Context appContext = context.getApplicationContext();
       // Resources res = appContext.getResources();

   // CalcPanchang calc = CalcPanchang.getInstance(appContext);

    Calendar c = Calendar.getInstance(TimeZone.getDefault());
    int day = c.get(Calendar.DAY_OF_MONTH);
    int month = c.get(Calendar.MONTH);
    int year = c.get(Calendar.YEAR);
    int tzOffset = c.get(Calendar.ZONE_OFFSET);
    int minutesFrommidnight = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
    // int minutsset =MainActivity.mPreferences.getInt("KEY_ALARAM",0);
    // Log.d("key","midmin " + minutesFrommidnight);
    //  Log.d("key","setmin " + minutsset);
  //  if(calc!=null)
     //    str ="calc not null";
                 //calc.getPanchangNotify(year,month,day,tzOffset);
  //  else str = "calc is null";
/*        int diff = minutesFrommidnight - minutsset;
        if (diff <0) diff = -diff;
       // if( diff < 5 ) {
          //  if (MainActivity.mPreferences.getBoolean("KEY_ALRMSET", true))
                MainActivity.tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
*/
    Intent contentIntent = new Intent(context, MainActivity.class);

    PendingIntent contentPendingIntent = PendingIntent.getActivity
            (context, NOTIFICATION_ID, contentIntent, PendingIntent
                    .FLAG_UPDATE_CURRENT);
    // Build the notification
    NotificationCompat.Builder builder = new NotificationCompat.Builder
            (context, PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noti_panchang)
            .setContentTitle("panchang")
            .setContentText(str)
            .setContentIntent(contentPendingIntent)

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL);

    // Deliver the notification
    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    //}

    }
    private void deliverstandupNotification(Context context) {
        // Create the content intent for the notification, which launches
        // this activity
        Intent contentIntent = new Intent(context, MainActivity.class);

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent
                        .FLAG_UPDATE_CURRENT);
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti_panchang)
                .setContentTitle("panchang")
                .setContentText("standup notification from panchang")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Deliver the notification
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
