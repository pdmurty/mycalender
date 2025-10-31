package com.pdmurty.mycalender;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;


import java.util.Calendar;

import java.util.Locale;

/************ emulator
 takes default location from avd.conf in emulator folder under avd.
 set the lat/lons in the config file for default location when the emulator starts first time.
 clear the emulator data and restart. Hyderabad lat=17.37,lon=78.51
 ***********************/
public class MainActivity extends AppCompatActivity
        implements GetYear.OnButtonDone,
        SharedPreferences.OnSharedPreferenceChangeListener{
    private static final int REQUESTCURRENTLOC = 1;
    private static final int REQUESTDBLOC = 2;
    private static final int UPDATE_REQUEST =10;
    //private static final String PRIMARY_CHANNEL_ID = "panchang_notification_channel";
    private static final String NOTIFY_CHANNEL_ID = "panchang_notification";
    //private WorkManager mWorkManager;
    private DialogFragment dlg;
    private long currTime;
    private int tzOffset;
    private CalendarView cv = null;

    public static TextToSpeech tts;
    public static Location mCurrentLocation;
    //public static String   mCurrentLocName;
    CalcPanchang instancePanchang;
    public static SharedPreferences mPreferences;
    private int locselected=0;
    int curDay;
    AppUpdateInfo resultinfo;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        mCurrentLocation = new Location("");
        if(mPreferences.getInt("LOC_PREF",0)==0) GetCurLocation();
        LocalManager.setLocale(this);
        ApplyWindowInsets();

        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("KEY_STOP",false );
        edit.apply();
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
            ShowPanchang(year, month, day);
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

        CheckForUpdates();


    }

    void ApplyWindowInsets(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setStatusBarContrastEnforced(true);
        }
        if (Build.VERSION.SDK_INT >= 36) {
            // Additional configuration for Android 16 (API level 36)
            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        TextView tv = findViewById(R.id.locTxt);
        ViewCompat.setOnApplyWindowInsetsListener(
                tv, (v, windowInsets) -> {
                    Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                    // Apply the insets as a margin to the view. This solution sets only the
                    // bottom, left, and right dimensions, but you can apply whichever insets are
                    // appropriate to your layout. You can also update the view padding if that's
                    // more appropriate.
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    //mlp.leftMargin = insets.left;
                    //mlp.bottomMargin = insets.bottom;
                    mlp.topMargin = insets.top;

                    v.setLayoutParams(mlp);

                    // Return CONSUMED if you don't want the window insets to keep passing
                    // down to descendant views.
                    return WindowInsetsCompat.CONSUMED;
                }


        );

    }
   void CheckForUpdates(){

       final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
       Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
       appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
           @Override
           public void onSuccess(AppUpdateInfo result) {

               if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                       // This example applies an immediate update. To apply a flexible update
                       // instead, pass in AppUpdateType.FLEXIBLE
                       && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                   // Request the update.
                    resultinfo = result;
                    AppUpdateOptions options =  AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build();
                   try {
                      // appUpdateManager.startUpdateFlowForResult(resultinfo,AppUpdateType.IMMEDIATE,MainActivity.this,UPDATE_REQUEST);
                       appUpdateManager.startUpdateFlowForResult(resultinfo,MainActivity.this, options,UPDATE_REQUEST);
                   } catch (IntentSender.SendIntentException e) {
                       e.printStackTrace();
                   }

               }
       }
       });

   }
    void showReview() {

        final ReviewManager reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();

        Calendar c = Calendar.getInstance();
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
        @Override
        public void onComplete(@NonNull Task<ReviewInfo> task) {
            if (task.isSuccessful()) {
                // Getting the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task <Void> flow = reviewManager.launchReviewFlow(MainActivity.this, reviewInfo);
                flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("KEY_INSTALL_DAY",day);
                        editor.putInt("KEY_INSTALL_MONTH",month);
                        editor.apply();
                    }
                });
            }


        }
    });
  }
    private void hookCalender(Bundle savedInstanceState) {

        if (cv == null) cv = findViewById(R.id.calendarView);
        if (savedInstanceState != null) {
           // Calendar c = Calendar.getInstance();
            long date = savedInstanceState.getLong("SavedDate");
            cv.setDate(date /*c.getTimeInMillis()*/);
            String thithi = savedInstanceState.getString("Thithi");

                WriteText(thithi);


        }

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
           //int prevday=0;
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                   ShowPanchang(year, month, dayOfMonth);
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
        if(requestCode==REQUESTCURRENTLOC || requestCode==REQUESTDBLOC) {
            if (resultCode == Activity.RESULT_CANCELED)
                showSnackbar("Unable get current location, Set to default location");
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            UpdateUI();
           // }
        }
        if(requestCode==UPDATE_REQUEST){
            if (resultCode!= RESULT_OK)
                showSnackbar("Failed to update, Try again later !");
        }

 }

    @SuppressLint("NonConstantResourceId")
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




    public void ShowPanchang(int year, int month, int dayOfMonth) {

        String str = //instancePanchang.getEvent(year);
                instancePanchang.ShowPanchang(year, month, dayOfMonth, tzOffset);

            WriteText(str);

        if (mPreferences.getBoolean("KEY_VOICE", true) && curDay!=dayOfMonth) {
            String engine = tts.getDefaultEngine();
            if(engine != null )
                if(!engine.contains("google"))
                    showSnackbar("Set google as default TTS engine for clear speech");
            tts.setLanguage(new Locale(GetLocale()));
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
            curDay=dayOfMonth;
        }


        showReview();
        //return str;
    }


    void WriteText(String str) {

        TextView tv = findViewById(R.id.myTxt);
        tv.setTextColor(Color.BLUE);
        tv.setText(str);

    }

//creates repeat alaram broadcast reciever for the prefered time.
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

    public void createNotificationChannel() {

        // Create a notification manager object.
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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

    @SuppressLint("NonConstantResourceId")
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

    @SuppressLint("CommitTransaction")
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

             if(s != null && s.contentEquals("KEY_ALARAM")) {

                this.setUpalaramservice();
             }
            }
   // @RequiresApi(api = Build.VERSION_CODES.O)
    private void UpdateUI() {
Log.d("UI","upadteUI");
        SetLocationHeader();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        currTime = c.getTime().getTime();
        tzOffset = c.get(Calendar.ZONE_OFFSET);
        CalendarView cv= findViewById(R.id.calendarView);
        cv.setDate(c.getTimeInMillis());
        ShowPanchang(year, month, day);
    }

    private String GetLocale() {
       return mPreferences.getString("lan_style", "te");
    }

    private void showSnackbar(final String text) {
        View container = findViewById(R.id.mLay);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }


}
