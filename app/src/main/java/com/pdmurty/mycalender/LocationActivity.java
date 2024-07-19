package com.pdmurty.mycalender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocationActivity extends AppCompatActivity {

    private Location mCurrentLocation;
    FusedLocationProviderClient mLocationProvider ;
    private AddressResultReceiver mResultReceiver;
    static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 1;
    public static String   mCurrentLocName;
    public static SharedPreferences mPreferences;
    LocationRequest mLocationRequest;
    private Location currentLocation;
    private LocationCallback locationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private SettingsClient mSettingsClient;
    int SET_INTERVAL=1000;
    int FASTEST_INTERVAL=1000;
    boolean mLocUpdate =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.locactivity);
        mCurrentLocation = new Location("");
        mLocationProvider = new FusedLocationProviderClient(this);
        mResultReceiver= new AddressResultReceiver(new Handler());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        GetCurLocation();

    }
    //////////////////////////////////////////////////////////
    // get the curent  location by calling getlastlocation, it is fasster and we donot need updates.
    // in some cases getlast location returns a null location, no one requested location update sofar
    // if the last location is null start request for location updates
    private void GetCurLocation() {
        if(!checkPermissions())
        {

            requestPermissions();
        }
        else
            getLastLocation();

    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mLocationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {
                    mCurrentLocation=location;
                    //Log.d("locadd","get lastloc" +" lat=" + location.getLatitude() + " lon=" + location.getLongitude());
                    getAddress(location);
                }
                else {
                    //system does not have last location, no one made a location request sofar
                    // make alocation request now
                   //ProgressBar pbar =  findViewById(R.id.progressLoc);
                   //pbar.setVisibility(View.VISIBLE);

                   startLocationUpdates();
                }

            }
        });
    }
////////////location permissions//////////////
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(LocationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(LocationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
            else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


////////////////////////////Location update request/////////////

    private void createLocationCallback() {
      mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                if(mCurrentLocation!=null){
                    if(!mLocUpdate) {
                        mLocUpdate=true;
                       stopLocationUpdates();
                        getAddress(mCurrentLocation);
                    }
                }
            }
        };
    }
    private void createLocationRequest(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(SET_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

    }
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
       // Log.d("locadd","start loc update");
    findViewById(R.id.locwarn).setVisibility(View.VISIBLE);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //noinspection MissingPermission
                        //Log.d("locadd","startlocupdate on sucess");
                        mLocationProvider.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                          }
                            })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {

                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                            Toast.makeText(LocationActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    private void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        if(mLocUpdate) {
            mLocationProvider.removeLocationUpdates(mLocationCallback);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CHECK_SETTINGS && resultCode==RESULT_OK)
            if(requestCode==REQUEST_CHECK_SETTINGS && resultCode==RESULT_CANCELED)
            {  setResult(Activity.RESULT_CANCELED);
                finish();
            }
    }

    private void getAddress( Location loc) {
        //Log.d("locadd","curr location get address"+ "lat="+loc.getLatitude() +"; long=" + loc.getLongitude());
        if (!Geocoder.isPresent()) {
            showSnackbar(getString(R.string.no_geocoder_available));
            return;
        }

        // If the user pressed the fetch address button before we had the location,
        // this will be set to true indicating that we should kick off the intent
        // service after fetching the location.

        startIntentService(loc);



    }
    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService(Location loc) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAdressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, loc);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }
    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.loc);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
             if(resultCode==Constants.SUCCESS_RESULT) {
                mCurrentLocName = resultData.getString(Constants.RESULT_DATA_KEY);
                PersistLocName(mCurrentLocName);
                setResult(Activity.RESULT_OK);

            }
            else{
                showSnackbar("You are not connected to internet, unable to get location address, setting to default location.");
                setResult(Activity.RESULT_CANCELED);

            }

            finish();
        }
    }

    private void PersistLocName(String addressOutput) {
        Calendar c = Calendar.getInstance();
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString("KEY_LOCNAME", addressOutput);
        edit.putFloat("KEY_LON", (float)mCurrentLocation.getLongitude());
        edit.putFloat("KEY_LAT", (float)mCurrentLocation.getLatitude());
        edit.putInt("KEY_TZONE",c.get(Calendar.ZONE_OFFSET));
        edit.putInt("LOC_PREF",1);
        edit.commit();

    }

}