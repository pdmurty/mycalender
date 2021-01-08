package com.example.mycalender;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class SeekBarPreference extends Preference {

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("Pref","calllayout");
        setWidgetLayoutResource(R.layout.seekbarpreference);
        Log.d("Pref", "after layout");
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        Log.d("Pref", "VH");
        holder.itemView.setClickable(false); // disable parent click
       // View button = holder.findViewById(R.id.green);

       // button.setClickable(true); // enable custom view click
      /*  button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // persist your value here
            }
        });

       */
    }
}
