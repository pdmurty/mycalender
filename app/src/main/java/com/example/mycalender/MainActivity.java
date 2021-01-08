package com.example.mycalender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Switch;
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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import static android.content.pm.PackageManager.GET_META_DATA;
import static android.util.Log.d;


public class MainActivity extends AppCompatActivity
        implements GetYear.OnButtonDone {
    private static final int REQUESTCODE = 1;
    private static final String PRIMARY_CHANNEL_ID = "panchang_notification_channel";
    private NotificationManager mNotificationManager;

    private DialogFragment dlg;
    private long currTime;
    private int tzOffset;
    private CalendarView cv = null;
    private static int NOTIFICATION_ID = 0;
    private SoundPool spmonth;
    private SoundPool spday;
    private SoundPool sppaksha;
    String locale;
    public static TextToSpeech tts;
    int soundtrack;
    Locale myLocale;
    public static Location mCurrentLocation;
    public static String   mCurrentLocName;
    String currentLanguage = "en", currentLang;
    CalcPanchang instancePanchang;
    Bundle mSavedState;
    public static SharedPreferences mPreferences;
    String SharedPrefFile = "com.example.android.mycalender";
    private FusedLocationProviderClient mLocationProvider;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private AddressResultReceiver mResultReceiver;
    private int locselected=0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mCurrentLocation = new Location("");
        mLocationProvider = new FusedLocationProviderClient(this);
        mResultReceiver= new AddressResultReceiver(new Handler());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        locale = mPreferences.getString("lan_style", "te");
        setLocale(locale);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    tts.setLanguage(new Locale(locale));
                }
            }
        });
        Log.d("key", locale);
        //LoadSoundPool();
        instancePanchang = CalcPanchang.getInstance(this);
        Swlib.SWSetSidmode(1, 0, 0);
        dlg = new GetYear();
        Date dt = new Date();
        dt.setTime(currTime);
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        currTime = c.getTime().getTime();
        tzOffset = c.get(Calendar.ZONE_OFFSET);
         if (savedInstanceState == null) {
            ShowPanchang(year, month, day, tzOffset);

        }
        PersistTimeZone(tzOffset);

        if (mPreferences.getBoolean("KEY_ALRMSET", true))
            setUpalaramservice();
        hookCalender(savedInstanceState);

    }

    private void LoadSoundPool() {
        spday = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        spday.load(this, R.raw.ld1, 0);
        spday.load(this, R.raw.ld2, 0);
        spday.load(this, R.raw.ld3, 0);
        spday.load(this, R.raw.ld4, 0);
        spday.load(this, R.raw.ld5, 0);
        spday.load(this, R.raw.ld6, 0);
        spday.load(this, R.raw.ld7, 0);
        spday.load(this, R.raw.ld8, 0);
        spday.load(this, R.raw.ld9, 0);
        spday.load(this, R.raw.ld10, 0);
        spday.load(this, R.raw.ld11, 0);
        spday.load(this, R.raw.ld12, 0);
        spday.load(this, R.raw.ld13, 0);
        spday.load(this, R.raw.ld14, 0);
        spday.load(this, R.raw.ld15, 0);
        spday.load(this, R.raw.ld16, 0);
        sppaksha = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        sppaksha.load(this, R.raw.lp0, 0);
        sppaksha.load(this, R.raw.lp1, 0);

        spmonth = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        spmonth.load(this, R.raw.lm1, 0);
        spmonth.load(this, R.raw.lm2, 0);
        spmonth.load(this, R.raw.lm3, 0);
        spmonth.load(this, R.raw.lm4, 0);
        spmonth.load(this, R.raw.lm5, 0);
        spmonth.load(this, R.raw.lm6, 0);
        spmonth.load(this, R.raw.lm7, 0);
        spmonth.load(this, R.raw.lm8, 0);
        spmonth.load(this, R.raw.lm9, 0);
        spmonth.load(this, R.raw.lm10, 0);
        spmonth.load(this, R.raw.lm11, 0);
        spmonth.load(this, R.raw.lm12, 0);

    }

    private void hookCalender(Bundle savedInstanceState) {

        if (cv == null) cv = findViewById(R.id.calendarView);
        Log.d("LOC","hkcl");
        if (savedInstanceState != null) {
            long date = savedInstanceState.getLong("SavedDate");
            cv.setDate(date);
            String thithi = savedInstanceState.getString("Thithi");
            WriteText(thithi);
        }

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String panchang = ShowPanchang(year, month, dayOfMonth, tzOffset);

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
        Log.d("LOC","onsaveinstance");
        long date = cv.getDate();
        TextView tv = findViewById((R.id.myTxt));
        String thithi = tv.getText().toString();
        outState.putLong("SavedDate", date);
        outState.putString("Thithi", thithi);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Location loc = new Location("");
            String geoname = data.getStringExtra("GEONAME");
            loc.setLongitude( data.getDoubleExtra("LONG", 0));
            loc.setLatitude(data.getDoubleExtra("LAT", 0));
            double tzoffset = data.getDoubleExtra("TZONE", 0);
            PersistLocation( loc);
            PersistLocName(geoname);
            PersistTimeZone( (int)(tzoffset*3600000) );
            UpdateUI();
        }


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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }
    private void SetLocationHeader(){

       TextView tv=  findViewById(R.id.locTxt);
       tv.setText(mPreferences.getString("KEY_LOCNAME","HYDERABAD"));

    }
    private void GetCurLocation() {

        if(!checkPermissions())
        {

            requestPermissions();
        }
        else
            getLastLocation();
        PersistTimeZone(tzOffset);
    }
    private void ShowSettings() {

        Intent prefIntent = new Intent(this, SettingsActivity.class);
        startActivity(prefIntent);

    }

    private void showGeoDb() {

      Intent request = new Intent(MainActivity.this, Geolocations.class);
      startActivityForResult(request, REQUESTCODE);
      GetCurLocation();

    }
    private void onMenuLocation(){
        LocationDlgFragment locDlg = new LocationDlgFragment();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("SetLocation") == null)
            locDlg.show(getSupportFragmentManager(), "SetLocation");

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

        String str = instancePanchang.ShowPanchang(year, month, dayOfMonth, tzOffset);
        WriteText(str);
        if (mPreferences.getBoolean("KEY_VOICE", true)) {
            tts.setLanguage(new Locale(locale));
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
        return str;
    }

    void WriteText(String str) {

        TextView tv = findViewById(R.id.myTxt);
        tv.setTextColor(Color.BLUE);
        tv.setTextSize(18);
        tv.setText(str);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setUpalaramservice() {

        int alaramMinutes = mPreferences.getInt("KEY_ALARAM", 0);
        int alrmHour = alaramMinutes / 60;
        int alrmMinute = alaramMinutes % 60;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alrmHour);
        calendar.set(Calendar.MINUTE, alrmMinute);

        Intent notifyIntent = new Intent(this, AlaramReciever.class);
       // notifyIntent.setAction("com.example.mycalender.ACTION");

        final PendingIntent notifyPendingIntent =
                PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        long repeatInterval = 300000; //AlarmManager.INTERVAL_FIFTEEN_MINUTES; //.INTERVAL_DAY;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;
                //calendar.getTimeInMillis();

      //  alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, repeatInterval,
       //         notifyPendingIntent);
        alarmManager.setInexactRepeating
                (AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime, repeatInterval,
                        notifyPendingIntent);

        createNotificationChannel();

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

    public void setLocale(String localeName) {

        myLocale = new Locale(localeName);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, null);

    }

    private void setNewLocale(AppCompatActivity mContext, @LocalManager.LocaleDef String language) {
        LocalManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalManager.setLocale(base));
    }

    protected void resetTitles() {
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
            if (info.labelRes != 0) {
                setTitle(info.labelRes);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Location services
    private boolean checkPermissions() {

        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                       @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest() {

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        mLocationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                String loc;
               if(location!=null) {
                   loc = "Loclat:" + location.getLatitude();
                   loc += " - " + "LocLon:";
                   loc += location.getLongitude();
                   mCurrentLocation=location;
                   PersistLocation(location);
                   Log.d("LOC","call getaddress");
                   getAddress(location);
               }
               else showSnackbar("enable location on this device");

            }
        });
    }

    private void PersistLocation(Location location) {

        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putFloat("KEY_LON", (float)location.getLongitude());
        edit.putFloat("KEY_LAT", (float)location.getLatitude());
        edit.commit();
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.main_activity_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
    private void getAddress( Location loc) {


                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            showSnackbar(getString(R.string.no_geocoder_available));
                            return;
                        }

                        // If the user pressed the fetch address button before we had the location,
                        // this will be set to true indicating that we should kick off the intent
                        // service after fetching the location.

                            startIntentService(loc);



    }
    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService(Location loc) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Log.d("LOC","calling address service");
        Intent intent = new Intent(this, FetchAdressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, loc);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
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

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
          mCurrentLocName = resultData.getString(Constants.RESULT_DATA_KEY);
          // SetLocationHeader( );
           PersistLocName(mCurrentLocName);
           UpdateUI();
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
        ShowPanchang(year, month, day, tzOffset);
    }

    private void PersistLocName(String addressOutput) {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString("KEY_LOCNAME", addressOutput);
        edit.commit();
        TextView tv=  findViewById(R.id.locTxt);
        tv.setText(addressOutput);
    }
    private void PersistTimeZone(int tzoffsetmillisecs ) {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putInt("KEY_TZONE",tzoffsetmillisecs );
        edit.commit();

    }
   
}
