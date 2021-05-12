package com.pdmurty.mycalender;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.media.RatingCompat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.resources.TextAppearance;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class launcher extends AppCompatActivity {
    SharedPreferences mPreferences;
    static FusedLocationProviderClient mLocationProvider;
    private static final int REQUESTCODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(mPreferences.getBoolean("KEY_INIT", false)) {
            Intent mainintent = new Intent(this, MainActivity.class);
            startActivity(mainintent);
        }
        else {
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt("KEY_INSTALL_DAY",day);
            editor.putInt("KEY_INSTALL_MONTH",month);
            editor.commit();
            Intent mainintent = new Intent(this, InitActivity.class);
            startActivity(mainintent);
        }
        finish();
    }


}
