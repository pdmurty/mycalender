package com.pdmurty.mycalender;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlaramWorker extends Worker {
    private NotificationManager mNotificationManager;
    private static final String PRIMARY_CHANNEL_ID = "panchang_notification_channel";
    private static final String NOTIFY_CHANNEL_ID = "panchang_notification";
    private static int NOTIFICATION_ID =0;
    TextToSpeech tts;
    String str;
    SharedPreferences mPreferences;
    private static int iStart;

    public AlaramWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);


    }

    @NonNull
    @Override
    public Result doWork() {
            Context context = getApplicationContext();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int tzOffset = c.get(Calendar.ZONE_OFFSET);
        int H= c.get(Calendar.HOUR);
        int M= c.get(Calendar.MINUTE);
        Log.d("Dowork","Hr="+H+"Mn="+M);
                   deliverNotification(context,year,month,day,tzOffset);

        return Result.success();
    }

    @Override
    public void onStopped() {


        super.onStopped();
    }

    private void deliverNotification(Context context, int year, int month, int day, int tzOffset) {
        // Create the content intent for the notification, which launches
        // this activity

        Log.d("WM","del Notific");
        Context appContext = context.getApplicationContext();
        CalcPanchang calc = CalcPanchang.getInstance(appContext);
        str= calc.getPanchangNotify(year,month,day,tzOffset);

        if (mPreferences.getBoolean("KEY_ALRMSET", true))
            tts = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i != TextToSpeech.ERROR) {
                        tts.setLanguage(new Locale(mPreferences.getString("lan_style", "te")));
                        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                    }else str="tts error";
                }
            });


        Intent contentIntent = new Intent(context, MainActivity.class);

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent
                        .FLAG_UPDATE_CURRENT);
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, NOTIFY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti_panchang)
                .setContentTitle("panchang")
                .setContentText(str)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());


    }
    public void createNotificationChannel() {

        // Create a notification manager object.
        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "panchang notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifies every 15 minutes to " +
                    "stand up and walk");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
