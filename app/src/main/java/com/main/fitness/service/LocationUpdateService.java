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
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.main.fitness.data.Model.ParcelableGeoPoint;
import com.main.fitness.ui.fragments.GoogleMapFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LocationUpdateService extends Service implements LocationListener {

    private static final int UPDATE_INTERVAL = 10*1000; // 10 seconds
    private static final int FASTEST_INTERVAL = 2*1000; // 2 seconds
    private static final int MAX_WAIT_TIME = 1000;
    private static final List<GeoPoint> points = new ArrayList<>();


    protected FusedLocationProviderClient client;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
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

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
    }

    private void getLastLocation() {
        try {
            client.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLocation = task.getResult();
                        } else {
                            Log.w(TAG, "Failed to get location.");
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
        //get the location change in the background and put it in the list
        int lat = (int) (location.getLatitude() * 1E6);
        int lng = (int) (location.getLongitude() * 1E6);
        GeoPoint geoPoint = new GeoPoint(lat, lng);
        points.add(geoPoint);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Send the list of points to GoogleMapFragMent
        ArrayList<ParcelableGeoPoint> pointsExtra = new ArrayList<>();
        for (GeoPoint point : points) {
            pointsExtra.add(new ParcelableGeoPoint(point));
        }
        Intent intent = new Intent(LocationUpdateService.this, GoogleMapFragment.class);
        intent.putParcelableArrayListExtra("geopoints", pointsExtra);

        removeLocationUpdates();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }
}
