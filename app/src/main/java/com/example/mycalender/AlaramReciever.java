package com.example.mycalender;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlaramReciever extends BroadcastReceiver {


    private NotificationManager mNotificationManager;
    private static final String PRIMARY_CHANNEL_ID = "panchang_notification_channel";
    private static int NOTIFICATION_ID =0;
    TextToSpeech tts;
    String str;
    SharedPreferences mPreferences;
    @Override
    public void onReceive(Context context, Intent notifyintent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int tzOffset = c.get(Calendar.ZONE_OFFSET);
        int minutesFrommidnight = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
        int minutsset = mPreferences.getInt("KEY_ALARAM",0);
        int diff = minutesFrommidnight - minutsset;
        if (diff <0) diff = -diff;
        if( diff < 5 ) {

               deliverNotification(context,year,month,day,tzOffset);

        }

    }

    private void deliverNotification(Context context, int year, int month, int day, int tzOffset) {
        // Create the content intent for the notification, which launches
        // this activity

        Context appContext = context.getApplicationContext();
        CalcPanchang calc = CalcPanchang.getInstance(appContext);
        str= calc.getPanchangNotify(year,month,day,tzOffset);
        if (mPreferences.getBoolean("KEY_ALRMSET", true))
        tts = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    tts.setLanguage(new Locale(mPreferences.getString("lan_style", "te")));
                    Log.d("key", "tts initialised");
                    tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);

                }
            }
        });
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

      mNotificationManager.notify(NOTIFICATION_ID, builder.build());

    }

}
