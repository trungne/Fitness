package com.main.fitness.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.security.Provider;
import java.text.DecimalFormat;
import java.util.List;

public class LocationUpdateService extends Service implements LocationListener {

    private static final int UPDATE_INTERVAL = 10*1000; // 10 seconds
    private static final int FASTEST_INTERVAL = 2*1000; // 2 seconds
    private static final int MAX_WAIT_TIME = 1000;
    private static final String CHANNEL_ID = "channel_01";
    private static final String PACKAGE_NAME = "LocationUpdateService";
    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    private static final int NOTIFICATION_ID = 2657;

    protected FusedLocationProviderClient client;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private static final int PARTIAL_WAKE_LOCK = 1;
    private GoogleMap mMap;
    private Location prev;
    private Location current;
    private Location mLocation;
    private LocationCallback mLocationCallback;
    private PowerManager.WakeLock wakeLock;
    private double mapFragmentUserTravelledDistance;
    private TextView mapFragmentSelectedDistanceTextView;
    private TextView mapFragmentTravelledDistanceTextView;
    private NotificationManager mNotificationManager;
    private Handler mServiceHandler;
    private boolean mChangingConfiguration = false;

    private final String TAG = "LocationUpdateService";

    public LocationUpdateService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        client = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
    }

    private void onNewLocation(Location location) {
        mLocation = location;

        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void getLastLocation() {
        try {
            client.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            client.removeLocationUpdates(mLocationCallback);
            stopSelf();
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        startService(new Intent(getApplicationContext(), LocationUpdateService.class));
        try {
            client.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(MAX_WAIT_TIME);
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo service : runningProcesses) {
            if (service.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : service.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        //If your app is the process in foreground, then it's not in running in background
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(
                () -> {
                    while (true) {
                        try {
                            Thread.sleep(FASTEST_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        return START_STICKY;
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        //Update location
        prev = current;
        current = location;

        //Calculate distance
        mapFragmentUserTravelledDistance += ((prev.distanceTo(current)) / 1000);
        //Format them into shorter string, to save space
        DecimalFormat df = new DecimalFormat("#0.000");
        mapFragmentTravelledDistanceTextView.setText("" + df.format(mapFragmentUserTravelledDistance));

        //Draw the polyline
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(prev.getLatitude(),prev.getLongitude()), new LatLng(current.getLatitude(),current.getLongitude()))
                .width(10)
                .color(Color.CYAN));

        //Move the camera to the new location point
        if(getApplicationContext() != null){
            Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeLocationUpdates();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }
}
