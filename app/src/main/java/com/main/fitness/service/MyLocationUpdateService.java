package com.main.fitness.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.main.fitness.R;
import com.main.fitness.ui.activities.MainActivity;
import com.main.fitness.ui.activities.RunningMapsActivity;

import org.greenrobot.eventbus.EventBus;

import java.time.LocalDateTime;

public class MyLocationUpdateService extends Service {
    private static final String TAG = "MyLocationUpdateService";
    //region data
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long MINIMUM_UPDATE_DISTANCE = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;

    private static final String CHANNEL_ID = "MyLocationUpdateService";
    private NotificationManager notificationManager;

    private Location latestLocation;
    //onCreate
    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        Log.e(TAG, "OnCreated");
    }


    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location currentLocation = locationResult.getLastLocation();
            if(latestLocation != null && latestLocation.distanceTo(currentLocation) < MINIMUM_UPDATE_DISTANCE){
                return;
            }

            latestLocation = currentLocation;

            Log.d("Locations update service is running: ", currentLocation.getLatitude() + "," + currentLocation.getLongitude());

            EventBus.getDefault().post(currentLocation);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prepareForegroundNotification();
        startLocationUpdates();
        Log.e(TAG, LocalDateTime.now().toString());
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        this.mFusedLocationClient.requestLocationUpdates(this.locationRequest,
                this.locationCallback, Looper.myLooper());
    }

    private void prepareForegroundNotification() {
        //Check for android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            this.notificationManager = getSystemService(NotificationManager.class);
            this.notificationManager.createNotificationChannel(serviceChannel);
        }

        //Create intent
        Intent notificationIntent = new Intent(this, RunningMapsActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                99,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);



        //Prepare the notification
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("fITness")
                .setContentText("We're counting your distance!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(100, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void initData() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        Log.e(TAG, "OnDestroy");
    }
}
