package com.pdmurty.mycalender;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/* time picker preferene code.
https://android.googlesource.com/platform/packages/apps/Settings/+/refs/heads/master/src/com/android/settings/datetime/TimePreferenceController.java
 */
public class SettingsActivity extends AppCompatActivity {
    public static final String EXTRA_PREFERENCES = SettingsActivity.class.getName() + ".EXTRA_PREFERENCES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putInt(EXTRA_PREFERENCES, getIntent().getIntExtra(EXTRA_PREFERENCES, 0));

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.pref_container, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }





}

