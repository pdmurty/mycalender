package com.example.mycalender;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;
import java.util.Date;

import static android.util.Log.d;


public class MainActivity extends AppCompatActivity
implements GetYear.OnButtonDone{
    private static final int REQUESTCODE = 1;
    private DialogFragment dlg;
    private long currTime;
    private int tzOffset;
    private CalendarView cv = null;
    private static int NOTIFICATION_ID =0;
    CalcPanchang instancePanchang;
    SharedPreferences mPreferences;
    String SharedPrefFile = "com.example.android.mycalender";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        d("calender", "on Create");
       instancePanchang = CalcPanchang.getInstance(this);
        Swlib.SWSetSidmode(1,0,0);
             dlg = new GetYear();
             Date dt = new Date();
             dt.setTime(currTime);
             Calendar c = Calendar.getInstance();
             int day = c.get(Calendar.DAY_OF_MONTH);
             int month = c.get(Calendar.MONTH);
             int year = c.get(Calendar.YEAR);
             currTime = c.getTime().getTime();
             tzOffset =  c.get(Calendar.ZONE_OFFSET);
        if(savedInstanceState==null) {
             ShowPanchang(year, month, day, tzOffset);
         }
        mPreferences = getSharedPreferences(SharedPrefFile, MODE_PRIVATE);
        setUpalaramservice();
        hookCalender(savedInstanceState);


        //androidx.preference.PreferenceManager.setDefaultValues(
       //         this, R.xml.preferenes, false);

    }

    private void hookCalender(Bundle savedInstanceState){
        if (cv ==null) cv = (CalendarView) findViewById(R.id.calendarView);
        if(savedInstanceState!=null){
            long date = savedInstanceState.getLong("SavedDate");
            cv.setDate(date);
            String thithi = savedInstanceState.getString("Thithi");
            WriteText(thithi);
        }

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String panchang = ShowPanchang(year , month, dayOfMonth,tzOffset);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            String geoname = data.getStringExtra("GEONAME");
            double lon = data.getDoubleExtra("LONG", 0);
            double lat = data.getDoubleExtra("LAT", 0);
            double tzoffset = data.getDoubleExtra("TZONE",0);
           // geoname += " lat: " + lat + " lon: " + lon + "tZONE: " + tzoffset;
           // DisplayToast(geoname);
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_Order:SetYear();break;
            case R.id.action_reset: cv.setDate(currTime);break;
            case R.id.action_status:showGeoDb();  break;
            case R.id.action_settings: ShowSettings();  break;
            default: break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void ShowSettings() {

        Intent prefIntent = new Intent(this, SettingsActivity.class);
        startActivity(prefIntent);

    }

    private void showGeoDb() {

        Intent request = new Intent(MainActivity.this,Geolocations.class);
        startActivityForResult(request,REQUESTCODE);
    }

    public void calenderClick(View view) {
            SetYear();

    }
    public void SetYear(){
        FragmentManager fm = getSupportFragmentManager();
        if(fm.findFragmentByTag("Set Year") ==null)
        dlg.show(getSupportFragmentManager(), "Set Year");
    }
    public void SetCalenderDate(int year, int month){
        if(year>1900 && year <2100) {
           double jd1970 = Swlib.GetJulDay(1970,1,1,0);
           double jdcur = Swlib.GetJulDay(year,month+1,1,0);
           jdcur -= jd1970;
           long date = 86400000;
           date *= (long)jdcur;
           cv.setDate(date);
        }
    }

    @Override
    public void OnGoButtonClicked(int year, int month) {
         SetCalenderDate(year,month);
         dlg.dismiss();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
       long date = cv.getDate();
       TextView tv = (TextView) findViewById((R.id.myTxt));
       String thithi = tv.getText().toString();
       outState.putLong("SavedDate",date);
       outState.putString("Thithi",thithi);


    }
    public void DisplayToast(String msg){
        Toast t  = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        t.show();

    }
    public String ShowPanchang(int year , int month, int dayOfMonth, int tzoff){
        String str = instancePanchang.ShowPanchang(year,month,dayOfMonth,tzOffset);
             WriteText(str);
             return str;
    }
    void WriteText(String str)
    {
        TextView tv = (TextView) findViewById(R.id.myTxt);
        tv.setTextColor(Color.BLUE);
        tv.setTextSize(18);
        tv.setText( str);
    }

    void setUpalaramservice(){

       int alrmHour =  mPreferences.getInt("HOUR",5);
       int alrmMinute = mPreferences.getInt("MINUTES",30);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alrmHour);
        calendar.set(Calendar.MINUTE,alrmMinute);

        Intent notifyIntent = new Intent(this,  AlaramReciever.class);
        final PendingIntent notifyPendingIntent =
                PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent,
                                                        PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long repeatInterval = AlarmManager.INTERVAL_DAY;
        long triggerTime =  calendar.getTimeInMillis();
         alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,triggerTime, repeatInterval,
                 notifyPendingIntent);
        AlarmManager.AlarmClockInfo clk;


    }

}
