package com.pdmurty.mycalender;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
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
    }

    public TimePreference(Context context, AttributeSet attrs,int defStyleAttr) {

        this(context, attrs, defStyleAttr, 0);

    }
    public TimePreference(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public int getTime() {
        return mTime;
    }
    public void setTime(int persisttime) {
        mTime = persisttime;
        String strampm = " AM";
        // Save to Shared Preferences
        persistInt(mTime);
        if(mTime>720){ mTime-=720; strampm= " PM";}
        setSummary(String.format("%2d:%02d %s",(int)mTime/60,(int)mTime%60,strampm));
    }

    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue( Object defaultValue) {
        if(defaultValue!=null)
           mTime = Integer.parseInt(defaultValue.toString());
        setTime(getPersistedInt(mTime));
    }

    @Override
    public int getDialogLayoutResource() {
        return  mDialogLayoutResId;
    }

}
