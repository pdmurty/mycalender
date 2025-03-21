package com.pdmurty.mycalender;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class  TimePreferenceDialogFragmentCompat
       extends PreferenceDialogFragmentCompat {

    private TimePicker mTimePicker;

    @Override
   public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // generate value to save
            int hours = mTimePicker.getCurrentHour();
            int minutes = mTimePicker.getCurrentMinute();
            int minutesAfterMidnight = (hours * 60) + minutes;

            // Get the related Preference and save the value
            DialogPreference preference = getPreference();
            if (preference instanceof TimePreference) {
                TimePreference timePreference =
                        ((TimePreference) preference);
                // This allows the client to ignore the user value.
                if (timePreference.callChangeListener(
                        minutesAfterMidnight)) {
                    // Save the value
                    timePreference.setTime(minutesAfterMidnight);
                }

            }
        }
   }

   public static TimePreferenceDialogFragmentCompat newInstance(
           String key) {
       final TimePreferenceDialogFragmentCompat
               fragment = new TimePreferenceDialogFragmentCompat();
       final Bundle b = new Bundle(1);
       b.putString(ARG_KEY, key);
       fragment.setArguments(b);

       return fragment;
   }

   @Override
   protected void onBindDialogView(View view) {
       super.onBindDialogView(view);

       mTimePicker = (TimePicker) view.findViewById(R.id.edit);

       // Exception when there is no TimePicker
       if (mTimePicker == null) {
           throw new IllegalStateException("Dialog view must contain" +
                   " a TimePicker with id 'edit'");
       }

       // Get the time from the related Preference
       Integer minutesAfterMidnight = null;
       DialogPreference preference = getPreference();
       if (preference instanceof TimePreference) {
           minutesAfterMidnight =
                   ((TimePreference) preference).getTime();
       }

       // Set the time to the TimePicker
       if (minutesAfterMidnight != null) {
           int hours = minutesAfterMidnight / 60;
           int minutes = minutesAfterMidnight % 60;
           boolean is24hour = DateFormat.is24HourFormat(getContext());

           mTimePicker.setIs24HourView(is24hour);
           mTimePicker.setCurrentHour(hours);


           mTimePicker.setCurrentMinute(minutes);

       }
   }

    public static class TimePrefV7dlgFragmentCompat {
    }
}
