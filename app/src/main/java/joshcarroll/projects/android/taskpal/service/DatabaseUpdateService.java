package joshcarroll.projects.android.taskpal.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import joshcarroll.projects.android.taskpal.database.DBHandler;
import joshcarroll.projects.android.taskpal.database.VersionDbHandler;

/**
 * Created by Josh on 29/08/2018.
 */

public class DatabaseUpdateService extends Service {

    private String TAG = getClass().getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"TIMER SERVICE STARTED!!");

                Calendar now = Calendar.getInstance();
                Calendar ThirtyDaysInHours = Calendar.getInstance();

                ThirtyDaysInHours.add(Calendar.HOUR, 720);
                ThirtyDaysInHours.set(Calendar.MINUTE, 0);
                ThirtyDaysInHours.set(Calendar.SECOND, 0);

                long difference = ThirtyDaysInHours.getTimeInMillis() - now.getTimeInMillis();

                Log.d(TAG, "Difference = " + difference);
                new CountDownTimer(difference, 1000) {

                    public void onTick(long millisUntilFinished) {

                        millisUntilFinished = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                        Log.d(TAG, "minutes remaining til update" + millisUntilFinished);
                    }

                    public void onFinish() {
                        //update database
                        //getting instance of current database
                        VersionDbHandler dbHandler = new VersionDbHandler(getApplicationContext());
                        int version = dbHandler.getVersionNumber();
                        dbHandler.setVersionNumber(++version);

                        DBHandler db = new DBHandler(getApplicationContext(), ++version);
                        db.getReadableDatabase();
                        Log.d(TAG, "Version = " + version);
                    }
                }.start();
            }
        });
        return START_NOT_STICKY;
    }
}
