package joshcarroll.projects.android.taskpal.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.database.VersionDbHandler;
import joshcarroll.projects.android.taskpal.service.LocationService;

public class SettingsActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private String TAG = "SETTINGS_FRAGMENT";
    private final int ACCESS_FINE_LOCATION_REQUEST_CODE = 1;

//    public static SettingsActivity newInstance(){
//
//        return new SettingsActivity();
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        android.app.ActionBar actionBar = this.getActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        VersionDbHandler versionDbHandler = new VersionDbHandler(getApplicationContext());
        int version = versionDbHandler.getVersionNumber();
        dbHandler = new DBHandler(getApplication(), ++version);
        TextView notificationTextview = findViewById(R.id.id_text_view_notifications);
        SwitchCompat switchCompat = findViewById(R.id.id_notifications_switch);

        if(dbHandler.getNotificationSetting()== 1){
            switchCompat.setChecked(true);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    isPermissionGranted();
//                    checkIsLocationEnabled();
                    Snackbar.make(compoundButton, "Notifications On", Snackbar.LENGTH_SHORT).show();

                }else{
                    stopService();
                    Snackbar.make(compoundButton,  "Notifications Off", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void startService(){
        startService(new Intent(getApplicationContext(), LocationService.class));
        dbHandler.setNotification(1);
    }

    public void stopService(){
        stopService(new Intent(getApplicationContext(), LocationService.class));
        dbHandler.setNotification(0);
    }

    public void isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , ACCESS_FINE_LOCATION_REQUEST_CODE);

            } else {
                //permission already granted
                checkIsLocationEnabled();
            }
        }
        else{
            Log.v(TAG,"Permission was granted at install time");
            checkIsLocationEnabled();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case ACCESS_FINE_LOCATION_REQUEST_CODE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SettingsActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void checkIsLocationEnabled(){

        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsIsEnabled = false;

        try {
            gpsIsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(NullPointerException ex) {ex.printStackTrace();}

        if(gpsIsEnabled) {

            startService();
        }
        else{
            displayLocationSettingsRequest(getApplicationContext());
        }
    }
    public void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        startService();
                        Log.i(TAG, "All location settings are satisfied.");
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SettingsActivity.this, 1);

                            Log.i(TAG, "status:"+status.getStatus());
                        } catch (IntentSender.SendIntentException sie) {
                            sie.printStackTrace();
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_OK){
            startService();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
