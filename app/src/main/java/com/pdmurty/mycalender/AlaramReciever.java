package com.pdmurty.mycalender;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.legacy.content.WakefulBroadcastReceiver;
import androidx.preference.PreferenceManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.POWER_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class AlaramReciever extends BroadcastReceiver
{
    private static String nexttrigger;
    private NotificationManager mNotificationManager;
    private static final String PRIMARY_CHANNEL_ID = "panchang_notification_channel";
    private static final String NOTIFY_CHANNEL_ID = "panchang_notification";
    private static int NOTIFICATION_ID =0;
    TextToSpeech tts;
    String str;
    SharedPreferences mPreferences;
    PowerManager.WakeLock wakeLock;
    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;
    private final float[] mRotationMatrix = new float[9];
    private final float[] pRotationMatrix = new float[9];
    private int mtrace=0;
    private int sensorcounter=0;
    private boolean bsavePrevtrace=false;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent notifyintent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        PowerManager mgr=(PowerManager)context.getSystemService(POWER_SERVICE);
        wakeLock=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getSimpleName());
        wakeLock.acquire(180000);

        mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int tzOffset = c.get(Calendar.ZONE_OFFSET);
        int hour = c.get(Calendar.HOUR_OF_DAY) ;
        int minits = c.get(Calendar.MINUTE);
        deliverNotification(context,year,month,day,tzOffset);
        c.add(Calendar.HOUR_OF_DAY,24);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY) ;
        minits = c.get(Calendar.MINUTE);
        SetupNextAlaramClock( context,c.getTimeInMillis());
       // setupNextAlaram(context,c.getTimeInMillis());


    }

    private void deliverNotification(final Context context, int year, int month, int day, int tzOffset) {
        // Create the content intent for the notification, which launches
        // this activity
        final Context appContext = context.getApplicationContext();
        CalcPanchang calc = CalcPanchang.getInstance(appContext);
        str= calc.getPanchangNotify(year,month,day,tzOffset);
        if (mPreferences.getBoolean("KEY_ALRMSET", true))
        tts = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {

                    tts.setLanguage(new Locale(mPreferences.getString("lan_style", "te")));
                    tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                    tts.speak(str, TextToSpeech.QUEUE_ADD, null);
                    tts.speak(str, TextToSpeech.QUEUE_ADD, null);

                }
                else {
                    str = "tts-error";

                }

            }
        });


    Intent contentIntent = new Intent(context, MainActivity.class);

    PendingIntent contentPendingIntent = PendingIntent.getActivity
            (context, NOTIFICATION_ID, contentIntent, PendingIntent
                    .FLAG_UPDATE_CURRENT);
    // Build the notification
    NotificationCompat.Builder builder = new NotificationCompat.Builder
            (context, NOTIFY_CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_noti_panchang)
            .setContentTitle("Panachangam")
            .setContentText(str)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL);

      mNotificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    static void setupNextAlaram(Context context, long triggertime){
        Intent notifyIntent = new Intent(context, com.pdmurty.mycalender.AlaramReciever.class);
         final PendingIntent notifyPendingIntent;

       notifyPendingIntent
       = PendingIntent.getBroadcast
                (context, NOTIFICATION_ID, notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null)
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle
                        (AlarmManager.RTC_WAKEUP,
                                triggertime,
                                notifyPendingIntent);
           }
            else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,triggertime,notifyPendingIntent);
            else
                alarmManager.set(AlarmManager.RTC_WAKEUP,triggertime,notifyPendingIntent);


    }
    static void SetupNextAlaramClock(Context context, long triggertime){
        Intent notifyIntent = new Intent(context, AlaramReciever.class);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (context, NOTIFICATION_ID, notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null)
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {

                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(triggertime, null);
                alarmManager.setAlarmClock(alarmClockInfo, notifyPendingIntent);
            }
            else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggertime, notifyPendingIntent);
            }
            else {

                alarmManager.set(AlarmManager.RTC_WAKEUP, triggertime, notifyPendingIntent);
            }

    }



}
