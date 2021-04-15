package com.pdmurty.mycalender;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.snackbar.Snackbar;

public class InitActivity extends AppCompatActivity {
    SharedPreferences mPreferences;
    static FusedLocationProviderClient mLocationProvider;
    private static final int REQUESTCODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }
    public void onClickAgree(View view) {
        //Intent prefer = new Intent(this,SettingsActivity.class);

        SharedPreferences.Editor edit =
                PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putBoolean("KEY_INIT",true);
        edit.commit();
        Intent locintent = new Intent(this, LocationActivity.class);
        startActivityForResult(locintent,REQUESTCODE);


        findViewById(R.id.butAgree).setVisibility(View.GONE);
        findViewById(R.id.butdisAgree).setVisibility(View.GONE);
        findViewById(R.id.eula).setVisibility(View.GONE);
        TextView tv = findViewById(R.id.welcome);
        tv.setTextSize(12);
        tv.setText(" Select your preferences and set your name in your prefered language, you can change the preference anytime later ");
    }

    public void onClickdisagree(View view) {
        this.finish();
    }
    public void onClickPreferences(View view) {
        Intent mainintent = new Intent(this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode== Activity.RESULT_CANCELED)
        Snackbar.make(findViewById(R.id.launcher_activity),
                "Unable to get current location, Set to default location", Snackbar.LENGTH_LONG).show();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.pref_launcher, new SettingsFragment())
                .commit();
        findViewById(R.id.pref).setVisibility(View.VISIBLE);

    }

}