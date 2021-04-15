package com.pdmurty.mycalender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements GetYear.OnButtonDone,
        SharedPreferences.OnSharedPreferenceChangeListener{
    private static final int REQUESTCURRENTLOC = 1;
    private static final int REQUESTDBLOC = 2;
    private static final String PRIMARY_CHANNEL_ID = "panchang_notification_channel";
    private static final String NOTIFY_CHANNEL_ID = "panchang_notification";
    private NotificationManager mNotificationManager;
    private WorkManager mWorkManager;
    private DialogFragment dlg;
    private long currTime;
    private int tzOffset;
    private CalendarView cv = null;
    private static int NOTIFICATION_ID = 0;
    String locale;
    public static TextToSpeech tts;
    public static Location mCurrentLocation;
    public static String   mCurrentLocName;
    CalcPanchang instancePanchang;
    public static SharedPreferences mPreferences;
    private int locselected=0;
    int curDay;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurrentLocation = new Location("");
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferences.registerOnSharedPreferenceChangeListener(this);
        //this situation is ulikely but safe side
        mCurrentLocation = new Location("");

        if(mPreferences.getInt("LOC_PREF",0)==0) GetCurLocation();

        LocalManager.setLocale(this);
        setContentView(R.layout.activity_main);
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("KEY_STOP",false );
        edit.commit();
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    tts.setLanguage(new Locale(GetLocale()));
                }
            }
        });

        instancePanchang = CalcPanchang.getInstance(this);
        Swlib.SWSetSidmode(1, 0, 0);
        dlg = new GetYear();
        Calendar c = Calendar.getInstance();
         int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        currTime = c.getTime().getTime();
        tzOffset = c.get(Calendar.ZONE_OFFSET);
         if (savedInstanceState == null) {
            ShowPanchang(year, month, day, tzOffset);
            createNotificationChannel();
        }
        if (mPreferences.getBoolean("KEY_ALRMSET", true))
           setUpalaramservice();
        hookCalender(savedInstanceState);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "feedback, email:pdmurty@hotmail.com, whatsapp:9490215228", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void hookCalender(Bundle savedInstanceState) {

        if (cv == null) cv = findViewById(R.id.calendarView);
        if (savedInstanceState != null) {
            Calendar c = Calendar.getInstance();


            long date = savedInstanceState.getLong("SavedDate");
            cv.setDate(c.getTimeInMillis());
            String thithi = savedInstanceState.getString("Thithi");
            WriteText(thithi);


        }

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
           int prevday=0;
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                   ShowPanchang(year, month, dayOfMonth, tzOffset);

            }

        });
        SetLocationHeader();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        long date = cv.getDate();
        TextView tv = findViewById((R.id.myTxt));
        String thithi = tv.getText().toString();
        outState.putLong("SavedDate", date);
        outState.putString("Thithi", thithi);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

          if(resultCode == Activity.RESULT_CANCELED)
          showSnackbar("Unable get current location, Set to default location");
              UpdateUI();
 }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_Order:
                SetYear();
                break;
            case R.id.action_reset:
                cv.setDate(currTime);
                break;
            case R.id.action_status:
                onMenuLocation();
                break;
            case R.id.action_settings:
                ShowSettings();
                break;
            case R.id.action_eph:
                ShowEphemiris();
                break;
            case R.id.action_Tabs:
                Intent eventIntent = new Intent(this, ShowEvents.class);
                startActivity(eventIntent);
            break;
            case R.id.action_help:
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                break;

                default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }
    private void SetLocationHeader(){

       TextView tv=  findViewById(R.id.locTxt);
       tv.setText(mPreferences.getString("KEY_LOCNAME","Hyderabad,India(default)"));

    }

    private void ShowSettings() {

       Intent prefIntent = new Intent(this, SettingsActivity.class);
       startActivity(prefIntent);

    }

    private void ShowEphemiris() {

        Intent ephIntent = new Intent(this, CalcEphemeris.class);
        startActivity(ephIntent);
    }



    public void calenderClick(View view) {
        if (tts.isSpeaking()) tts.stop();
        else SetYear();

    }

    public void SetYear() {

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("Set Year") == null)
            dlg.show(getSupportFragmentManager(), "Set Year");
    }

    public void SetCalenderDate(int year, int month) {
        if (year > 1900 && year < 2100) {
            double jd1970 = Swlib.GetJulDay(1970, 1, 1, 0);
            double jdcur = Swlib.GetJulDay(year, month + 1, 1, 0);
            jdcur -= jd1970;
            long date = 86400000;
            date *= (long) jdcur;
            cv.setDate(date);
        }
    }

    @Override
    public void OnGoButtonClicked(int year, int month) {
        SetCalenderDate(year, month);
        dlg.dismiss();
    }



    public void DisplayToast(String msg) {
        Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        t.show();

    }

    public String ShowPanchang(int year, int month, int dayOfMonth, int tzoff) {

        String str = //instancePanchang.getEvent(year);
                instancePanchang.ShowPanchang(year, month, dayOfMonth, tzOffset);
        WriteText(str);
        if (mPreferences.getBoolean("KEY_VOICE", true) && curDay!=dayOfMonth) {
           tts.setLanguage(new Locale(GetLocale()));
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
            curDay=dayOfMonth;
        }
        return str;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void WriteText(String str) {

        TextView tv = findViewById(R.id.myTxt);
       // tv.setFontVariationSettings("'wdth' 150");
        tv.setTextColor(Color.BLUE);
       // tv.setTextSize(8);
        tv.setText(str);

    }


    void setUpalaramservice() {

        int alaramMinutes = mPreferences.getInt("KEY_ALARAM", 300);
        int alrmHour = alaramMinutes / 60;
        int alrmMinute = alaramMinutes % 60 ;
        Calendar calendardue = Calendar.getInstance();
        calendardue.set(Calendar.HOUR_OF_DAY, alrmHour);
        calendardue.set(Calendar.MINUTE, alrmMinute);
        Calendar calendarcur = Calendar.getInstance();
        if(calendardue.before(calendarcur))
            calendardue.add(Calendar.HOUR_OF_DAY, 24);
        long triggerTime =   calendardue.getTimeInMillis();
        AlaramReciever.SetupNextAlaramClock(this, triggerTime);
        createNotificationChannel();
    }
    void setUpAlaramWork(){

        int alaramMinutes = mPreferences.getInt("KEY_ALARAM", 300);
        int alrmHour = alaramMinutes / 60;
        int alrmMinute = alaramMinutes % 60;
        Calendar calendardue = Calendar.getInstance();
        long curtime = calendardue.getTimeInMillis();
        calendardue.set(Calendar.HOUR_OF_DAY, alrmHour);
        calendardue.set(Calendar.MINUTE, alrmMinute);
        Calendar calendarcur = Calendar.getInstance();
        if(calendardue.before(calendarcur)){
            calendardue.add(Calendar.HOUR_OF_DAY, 24);
        }
        long delay = calendardue.getTimeInMillis()-calendarcur.getTimeInMillis();


        PeriodicWorkRequest alaramRequest =
                new  PeriodicWorkRequest.Builder(AlaramWorker.class,
                        1,TimeUnit.HOURS,5,TimeUnit.MINUTES
                        )
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(getApplicationContext())
                .cancelAllWork();
        WorkManager.getInstance(getApplicationContext()).enqueue(alaramRequest);

 }
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (NOTIFY_CHANNEL_ID,
                            "panchang notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifies panchang " );
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void GetCurLocation() {
        Intent locintent = new Intent(this, LocationActivity.class);
        startActivityForResult(locintent,REQUESTCURRENTLOC);

    }


    private void showGeoDb() {

        Intent request = new Intent(MainActivity.this, Geolocations.class);
        startActivityForResult(request, REQUESTDBLOC);


    }
    private void onMenuLocation(){
        LocationDlgFragment locDlg = new LocationDlgFragment();
       locDlg.setLocselected( mPreferences.getInt("LOC_PREF", 0));
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("SetLocation") == null)
            locDlg.show(getSupportFragmentManager(), "SetLocation");

    }

    public void onClickLOC(View view) {

        switch(view.getId()) {
            case R.id.locCur:
                locselected=1;
                // Pirates are the best
                break;
            case R.id.locDb:
                locselected=2;
                // Ninjas rule
                break;
            case R.id.locMap:
                locselected=3;
                // Ninjas rule
                break;
            case R.id.ok:
                OnLocationSelected(locselected);
                break;
            case R.id.cancel:
                OnLocationSelected(0);
                break;
        }

    }

    public void OnLocationSelected(int locSelection) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction();
        LocationDlgFragment dlg = (LocationDlgFragment) fm.findFragmentByTag("SetLocation");
        if(dlg!=null) dlg.dismiss();

        switch (locSelection){
            case 0:
                break;
            case 1: GetCurLocation();
                break;
            case 2: showGeoDb();
                break;
            case 3:
                showMap();
                break;

        }



    }

    private void showMap() {
        Intent mapInetent = new Intent(this,Geomaps.class);
        startActivity(mapInetent);
    }

    public void locClick(View view) {
        onMenuLocation();
    }

     @RequiresApi(api = Build.VERSION_CODES.M)
     @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

             if(s.contentEquals("KEY_ALARAM")) {

                this.setUpalaramservice();


             }
            }
    private void UpdateUI() {

        SetLocationHeader();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        currTime = c.getTime().getTime();
        tzOffset = c.get(Calendar.ZONE_OFFSET);
        CalendarView cv= findViewById(R.id.calendarView);
        cv.setDate(c.getTimeInMillis());
        ShowPanchang(year, month, day, tzOffset);
    }

    private String GetLocale() {
       return mPreferences.getString("lan_style", "te");
    }

    private void showSnackbar(final String text) {
        View container = findViewById(R.id.main_activity_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }


}
