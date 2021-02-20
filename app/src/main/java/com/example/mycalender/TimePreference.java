package com.example.mycalender;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;


public class TimePreference extends DialogPreference {

    private int lastHour=0;
    private int lastMinute=0;
    private TimePicker picker=null;
    private int mTime;
    private int mDialogLayoutResId = R.layout.pref_dialog_time;

    public TimePreference(Context context) {
        this(context, null);
    }
    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.dialogPreferenceStyle);
        Log.d("Pref", "TimePreference:1 ");

    }

    public TimePreference(Context context, AttributeSet attrs,int defStyleAttr) {

        this(context, attrs, defStyleAttr, 0);
        Log.d("Pref", "TimePreference:2 ");
    }
    public TimePreference(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.d("Pref", "TimePreference:3 ");
        // Do custom stuff here
        // ...
        // read attributes etc.
    }

    public int getTime() {
        return mTime;
    }
    public void setTime(int time) {
        mTime = time;
        String strampm = " AM";
        // Save to Shared Preferences
        persistInt(time);
        if(time>720){ time-=720; strampm= " PM";}
        setSummary(String.format("%2d:%2d %s",(int)time/60,(int)time%60,strampm));
    }
    /*
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());

        return(picker);
    }

    protected void onBindDialogView(View v) {
       // super.onBindDialogView(v);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    protected void onDialogClosed(boolean positiveResult) {
       // super.onDialogClosed(positiveResult);

        Log.d("time"," dialog close");
        if (positiveResult) {
            lastHour=picker.getCurrentHour();
            lastMinute=picker.getCurrentMinute();

            String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);

            if (callChangeListener(time)) {
               // persistString(time);

                setSummary("time");
            }
        }
    }

*/

    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue( Object defaultValue) {

        int persisttime = getPersistedInt(mTime);
        setTime(persisttime);


    }

    @Override
    public int getDialogLayoutResource() {
        return  mDialogLayoutResId;
    }

}
