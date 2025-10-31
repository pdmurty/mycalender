package com.pdmurty.mycalender;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
//import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Geolocations extends AppCompatActivity {
    private GeoVwModel mVwmodel;
    private RecyclerView recycler;
    private  GeoListAdapter mAdapter;
    List<Countries> mCountryList;
    List<Admins> mAdminList;
    ArrayAdapter<String> arrCountries;
    ArrayAdapter<String> arrAdmins;
    Spinner spCountries;
    private static String searchText = "";
    private static String countrycode = "";
    private static String admincode = "";
    int REQUESTCODE =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocations);
        ApplyWindowInsets();
        final EditText edt = findViewById(R.id.searchtxt);
        edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchText = v.getText().toString();
                UpdateGeonames();
                return false;
            }
        });

         spCountries = findViewById(R.id.countries);
         arrCountries=  new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item);
        if (spCountries!=null) {
            spCountries.setAdapter(arrCountries);

        }

        Spinner spAdmins = findViewById(R.id.admins);
        arrAdmins=  new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item);
        if (spAdmins!=null) {
            spAdmins.setAdapter(arrAdmins);
           }
        recycler = findViewById(R.id.recylerVW);
        mAdapter = new GeoListAdapter(this);

        recycler.setAdapter(mAdapter);

        recycler.setLayoutManager( new LinearLayoutManager(this));

        mVwmodel = new ViewModelProvider( this).get(GeoVwModel.class);

        UpdateCountryList();


        spCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               countrycode = mCountryList.get(position).CountryCode;
                mAdapter.SetCountryName( mCountryList.get(position).countryName);
                FillAdmins(position);
                UpdateGeonames();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spAdmins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                admincode = mAdminList.get(position).admin1code;
                searchText="";
                UpdateGeonames();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    void ApplyWindowInsets(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setStatusBarContrastEnforced(true);
        }
        if (Build.VERSION.SDK_INT >= 36) {
            // Additional configuration for Android 16 (API level 36)
            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        View tv = findViewById(R.id.countries);
        ViewCompat.setOnApplyWindowInsetsListener(
                tv, (v, windowInsets) -> {
                    Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                    // Apply the insets as a margin to the view. This solution sets only the
                    // bottom, left, and right dimensions, but you can apply whichever insets are
                    // appropriate to your layout. You can also update the view padding if that's
                    // more appropriate.
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    // mlp.leftMargin = insets.left;
                    //topM mlp.bottomMargin = insets.bottom;
                    mlp.topMargin = insets.top;

                    v.setLayoutParams(mlp);

                    // Return CONSUMED if you don't want the window insets to keep passing
                    // down to descendant views.
                    return WindowInsetsCompat.CONSUMED;
                }


        );

    }
    String GetSelectedCountry(){
       String strCountry = MainActivity.mPreferences.getString("KEY_LOCNAME","Hyderabad,India");
        return TextUtils.split(strCountry,",")[1];
    }
    private void FillAdmins(int position) {
        String countrycode = mCountryList.get(position).CountryCode;
        mVwmodel.getAllAdmins(countrycode).observe(this, new Observer<List<Admins>>() {
            @Override
            public void onChanged(List<Admins> admins) {
                arrAdmins.clear();
                for (int i =0; i<admins.size();++i)
                    arrAdmins.add(admins.get(i).adminname);
                arrAdmins.notifyDataSetChanged();
                mAdminList=admins;

            }
        });
    }

    private void UpdateCountryList() {
        mVwmodel.getAllCountries().observe(this, new Observer<List<Countries>>() {
            @Override
            public void onChanged(List<Countries> countries) {
                arrCountries.clear();
                String strCountryName ;
                String strSelCountryname = GetSelectedCountry();
                for (int i =0; i<countries.size();++i) {
                    strCountryName = countries.get(i).countryName;
                    arrCountries.add(strCountryName);
                     if(TextUtils.equals(strCountryName,strSelCountryname))
                        spCountries.setSelection(i);
                }
                arrCountries.notifyDataSetChanged();
                mCountryList=countries;

            }
        });
    }

    private void UpdateGeonames() {

        String search = searchText + "%" ;
        mVwmodel.getgeolocs(countrycode, admincode, search).observe(this, new Observer<List<GeoDao.GeoLoc>>() {
            @Override
            public void onChanged(List<GeoDao.GeoLoc> geoLocs) {
                mAdapter.SetGeoList(geoLocs);
            }
/*
            @Override
            public void onChanged(List<Geonames> geonames) {
                mAdapter.SetGeoList(geonames);

            }
            */

        });
    }

}