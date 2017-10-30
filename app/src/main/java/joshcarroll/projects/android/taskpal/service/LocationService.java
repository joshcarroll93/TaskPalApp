package joshcarroll.projects.android.taskpal.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import joshcarroll.projects.android.taskpal.R;
import joshcarroll.projects.android.taskpal.activity.MainActivity;
import joshcarroll.projects.android.taskpal.data.NewTask;
import joshcarroll.projects.android.taskpal.database.DBHandler;

/**
 * Created by Josh on 22/10/2017.
 */

public class LocationService extends Service {

    private String TAG = "LOCATION_SERVICE";
    public LocationManager mLocationManager;
    public MyLocationListener mLocationListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG," SERVICE STARTED!!");
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mLocationListener = new MyLocationListener();

                try{
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, mLocationListener);
                    Log.d(TAG, mLocationManager.toString());
                }catch(SecurityException se){
                    se.printStackTrace();
                }
            }
        });
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mLocationManager.removeUpdates(mLocationListener);

        }catch(SecurityException se){
            se.printStackTrace();
        }
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(final Location location) {

            Log.d(TAG, "Location changed: " + ""+location);

            DBHandler db = new DBHandler(getApplicationContext());
            List<NewTask> taskList = db.getAllTasks();

            Log.d("taskList size =" , ""+taskList.size());

            for(int i = 0; i < taskList.size(); i++){

                Log.d(TAG," Task LAT"+ taskList.get(i).getLatitude());
                Log.d(TAG," CURRENT LOCATION LAT: "+ location.getLatitude());
                Log.d(TAG," Task Lng: "+taskList.get(i).getLongitude());
                Log.d(TAG," CURRENT Lng: "+location.getLongitude());

                float[] distanceResults = new float[1];
                Location.distanceBetween(taskList.get(i).getLatitude(), taskList.get(i).getLongitude(),
                        location.getLatitude(), location.getLongitude(), distanceResults);

                double distance = distanceResults[0];
                Log.d(TAG," Distance = "+ distance+"m");

                if (distance < 75 && taskList.get(i).getStatus() == 0){

                    sendNotification(taskList.get(i));
                    taskList.get(i).setStatus(1);

                    db.updateStatus(taskList.get(i));
                    if(taskList.get(i).getStatus() == 1){
                        Log.d(TAG, "task removed");
                        taskList.remove(i);
                    }
                }
            }
        }
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "Provider Disabled: "+ provider);
        }
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "Provider Enabled: " + provider);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "Status Changed: " + provider);
        }
        private void sendNotification(NewTask task){

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            //.setSmallIcon(R.drawable.edit_icon)
                            .setContentTitle(task.getTitle())
                            .setContentText(task.getDescription())
                            .setLights(Color.RED, 2000, 2000)
                            .setVibrate(new long[] { 1000, 1000, 1000 })
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                            .setAutoCancel(true);

            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("Task", task);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(1, mBuilder.build());
        }
    }
}
