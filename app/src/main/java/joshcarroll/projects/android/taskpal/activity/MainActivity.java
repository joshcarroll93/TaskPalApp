package joshcarroll.projects.android.taskpal.activity;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.io.File;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.adapter.SectionsPagerAdapter;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.fragment.AddTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.DeleteTaskFragment;
import joshcarroll.projects.android.taskpal.fragment.SettingsFragment;
import joshcarroll.projects.android.taskpal.fragment.TabbedPlaceholderFragment;
import joshcarroll.projects.android.taskpal.fragment.ViewSingleTaskFragment;
import joshcarroll.projects.android.taskpal.listener.NewTaskListener;


public class MainActivity extends AppCompatActivity implements NewTaskListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
//    private List<NewTask> tasks;
    private String TAG = "MAIN_ACTIVITY";
    public static final int MY_PERMISSIONS_REQUEST_COURSE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHandler dbHandler = new DBHandler(getApplication());

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Log.d(TAG, "Location Permission Granted");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COURSE_LOCATION
                );
            }
        }

        if(doesDatabaseExist(getApplicationContext(), "ToDoList")){
            Log.d(TAG, "Table Exists");
        }
        else {
            Log.d(TAG, "Table does not exist");
        }
        if(dbHandler.isTableExists("Tasks", true)){

            Log.d("DB_HELPER", "Tasks does exist");
        }
        else{
            Log.d("DB_HELPER", "Tasks does NOT exist");
        }
        if(dbHandler.isTableExists("Settings", true)){

            Log.d("DB_HELPER", "Settings does exist");
        }
        else{
            Log.d("DB_HELPER", "Settings does NOT exist");
        }


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            //databaseSetup();
//            dbHandler.setNotification(0);
            NewTask task = new NewTask(0, "Title", "Description", 0.0, 0.0, "Address", 1);
            dbHandler.addTask(task);
            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.edit_icon_white);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddTaskFragment dialogFragment = new AddTaskFragment();
                dialogFragment.setListener(MainActivity.this);
                dialogFragment.show(getFragmentManager(), "ADD_TASK_FRAGMENT");
            }
        });

        if (getIntent().getExtras() != null) {

            NewTask task = getIntent().getParcelableExtra("Task");

            if(task != null){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(ViewSingleTaskFragment.newInstance(task), null);
                ft.commit();
            }

        }else{
            Log.d(TAG, "Parcelable Task is null");
        }
    }
    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        File file  = context.getDatabasePath("Settings");

        Log.d("MAIN_ACTIVITY", file.toString());
        return dbFile.exists();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        DBHandler dbHandler = new DBHandler(getApplication());

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COURSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "You can turn on notifications now", Toast.LENGTH_LONG).show();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    dbHandler.setNotification(0);
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar card_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            SettingsFragment settingsFragment = SettingsFragment.newInstance();

            settingsFragment.show(getFragmentManager(),"SETTINGS_FRAGMENT");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void addTask(NewTask newTask) {

        TabbedPlaceholderFragment.tasks.add(newTask);
        TabbedPlaceholderFragment.activeTasks.add(newTask);

        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyItemInserted(newTask.getId());
        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyDataSetChanged();

        if(newTask.getStatus() == 0){
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyItemInserted(newTask.getId());
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyDataSetChanged();
        }
        //if()

    }

    @Override
    public void removeTask(NewTask removedTask) {

        TabbedPlaceholderFragment.tasks.remove(removedTask);
        TabbedPlaceholderFragment.activeTasks.remove(removedTask);

        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyItemRemoved(removedTask.getId());
        mSectionsPagerAdapter.allTasksFragment.mListAdapter.notifyDataSetChanged();

        if(removedTask.getStatus() == 0){
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyItemInserted(removedTask.getId());
            mSectionsPagerAdapter.activeTasksFragment.mListAdapter.notifyDataSetChanged();
        }
    }

    public void showDeleteTaskFragment(NewTask task){

        DeleteTaskFragment deleteFragment = DeleteTaskFragment.newInstance(task);
        deleteFragment.setListener(MainActivity.this);
        deleteFragment.show(getFragmentManager(), "CONFIRM_DELETE_FRAGMENT");
    }
    public void copyTaskToClipboard(NewTask task){
        String getTask = task.getTitle() + "\n" + task.getDescription();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Task to copy", getTask);
        clipboard.setPrimaryClip(clip);
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
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, 1);
                        } catch (IntentSender.SendIntentException e) {
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
    public void checkIsLocationEnabled(){

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {ex.printStackTrace();}

        if(!gps_enabled) {
            displayLocationSettingsRequest(getApplication());
        }
    }
}
