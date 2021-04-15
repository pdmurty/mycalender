package com.pdmurty.mycalender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DoAlaram extends AppCompatActivity {
    TextToSpeech tts;
    String str;
    SharedPreferences mPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_alaram);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int tzOffset = c.get(Calendar.ZONE_OFFSET);
        CalcPanchang calc = CalcPanchang.getInstance(this);
        str= calc.getPanchangNotify(year,month,day,tzOffset);
        ((TextView)findViewById(R.id.message)).setText(str);
        if (mPreferences.getBoolean("KEY_ALRMSET", true))
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i != TextToSpeech.ERROR) {
                        tts.setLanguage(new Locale(mPreferences.getString("lan_style", "te")));
                        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                        tts.speak(str, TextToSpeech.QUEUE_ADD, null);
                        tts.speak(str, TextToSpeech.QUEUE_ADD, null);

                        tts.speak(str, TextToSpeech.QUEUE_ADD, null);
                        tts.speak(str, TextToSpeech.QUEUE_ADD, null);

                        tts.speak(str, TextToSpeech.QUEUE_ADD, null);
                        tts.speak(str, TextToSpeech.QUEUE_ADD, null);

                        tts.speak(str, TextToSpeech.QUEUE_ADD, null);
                        tts.speak(str, TextToSpeech.QUEUE_ADD, null);

                    }
                    else str = "tts-error";
                }
            });


    }

    public void onStop(View view) {


        this.finish();
    }
}