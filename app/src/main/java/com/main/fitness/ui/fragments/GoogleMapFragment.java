package com.main.fitness.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.main.fitness.R;

import java.util.ArrayList;
import java.util.Objects;

public class GoogleMapFragment extends Fragment implements SensorEventListener {

    private static final int UPDATE_INTERVAL = 10*1000; // 10 seconds
    private static final int FASTEST_INTERVAL = 2*1000; // 2 seconds
    private static final int MAX_WAIT_TIME = 1000;
    private static final int my_request_permission_code = 99;
    private final static long SAVE_OFFSET_TIME = AlarmManager.INTERVAL_HOUR;
    private final static int SAVE_OFFSET_STEPS = 500;
    private static int steps = 0;
    private static long lastSaveSteps;
    private static long lastSaveTime;

    //Used for location operations
    protected FusedLocationProviderClient client;
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private Sensor sensor;
    private SensorManager sensorManager;

    //Container for markers
    ArrayList<LatLng> locationArrayList;


    //xml
    private TextView mapFragmentSelectedDistanceTextView;
    private LinearLayout innerLinearLayoutMap;
    private Button mapFragmentButtonRun;
    private Button mapFragmentButtonStop;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            //Request permission location
            requestPermission();

            //Location monitor
            client = LocationServices.getFusedLocationProviderClient(requireActivity());
            mMap = googleMap;
            sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
            sensor= sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

            //Add function to zoom in and out on the map
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //Move the camera to the current user location
            moveToCurrentLocation();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_google_map,container,false);
        Bundle bundle = this.getArguments();
        String data;

        //Load the layout, textview, linearlayout, buttons
        mapFragmentSelectedDistanceTextView = v.findViewById(R.id.map_fragment_selected_distance);
        innerLinearLayoutMap = v.findViewById(R.id.map_inner_linear_layout);
        mapFragmentButtonRun = v.findViewById(R.id.map_fragment_button_run);
        mapFragmentButtonStop = v.findViewById(R.id.map_fragment_button_stop_run);


        //Actions for the buttons
        mapFragmentButtonRun.setOnClickListener(this::startRunningButton);
        mapFragmentButtonStop.setOnClickListener(this::stopRunningButton);

        //Set data and set layout for linear layout
        try{
            //Data for textview
            assert bundle != null;
            data = bundle.getString("selectedRunningDistance");
            mapFragmentSelectedDistanceTextView.setText(data);

        }
        catch(NullPointerException e){
            Toast.makeText(requireActivity(), "NullPointerException occurred !", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        float[] values = sensorEvent.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps = value;
            updateIfNecessary();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Request phone permission for location
    public void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 1);

    }

    //Move the view of the user to their current location
    @SuppressLint("MissingPermission")
    public void moveToCurrentLocation() {
        client.getLastLocation().addOnSuccessListener(location -> {

            try {
                //Add the custom marker on the location
                LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(lastLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 17));
                Toast.makeText(requireActivity(), "You are here", Toast.LENGTH_SHORT).show();
            }
            //If user does not enable GPS settings, they will get null result
            catch (NullPointerException e) {
                showErrorNoGPS();
            }
        }).addOnFailureListener(e -> Toast.makeText(requireActivity(), "Something wrong !", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("MissingPermission")
    public void setCurrentLocationWithCustomLogo(){
        client.getLastLocation().addOnSuccessListener(location -> {

            try {
                //Add the custom marker on the location
                LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(lastLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 17));
                Toast.makeText(requireActivity(), "You are here", Toast.LENGTH_SHORT).show();
            }
            //If user does not enable GPS settings, they will get null result
            catch (NullPointerException e) {
                showErrorNoGPS();
            }
        }).addOnFailureListener(e -> Toast.makeText(requireActivity(), "Something wrong !", Toast.LENGTH_SHORT).show());
    }

    private void updateIfNecessary() {
        if (steps > lastSaveSteps + SAVE_OFFSET_STEPS ||
                (steps > 0 && System.currentTimeMillis() > lastSaveTime + SAVE_OFFSET_TIME)) {
            lastSaveSteps = steps;
            lastSaveTime = System.currentTimeMillis();
        }
    }

    //Guide user to settings to enable GPS
    public void showErrorNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("No", (dialog, id) -> dialog.cancel())
                .setNegativeButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void showStopDialog() {
//        AlertDialog builder = new MaterialAlertDialogBuilder(requireContext())
//                .setMessage("Steps: " + steps + "\n.Do you want to stop running?")
//                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
//                .setPositiveButton("Accept", (dialog, id) ->
//                        startActivity(new Intent(getActivity(), GymFragment.class)))
//                .show();
//        builder.show();
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setMessage("Steps: " + steps + "\nDo you want to stop running?")
                .setNegativeButton("Keep Running", (dialog, id) -> dialog.cancel())
                .setPositiveButton("Accept", (dialog, id) ->
                        startActivity(new Intent(getActivity(), GymFragment.class)));
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //Start running button
    @SuppressLint("SetTextI18n")
    private void startRunningButton(View v){
        //Change the zoom in a-bit for closer look on the street
        mapFragmentButtonRun.setEnabled(false);
        mapFragmentButtonRun.setText("Running");

        //Initialize the Marker array
        locationArrayList = new ArrayList<>();

        //Change the logo on the user location
        mMap.clear();
        setCurrentLocationWithCustomLogo();
        mMap.animateCamera(CameraUpdateFactory.zoomBy(3));

        Toast.makeText(requireActivity(), "Location Update is now online", Toast.LENGTH_SHORT).show();
        startLocationUpdate();
    }

    @SuppressLint("SetTextI18n")
    private void stopRunningButton(View v) {
        showStopDialog();
//        mapFragmentButtonRun.setEnabled(true);
//        mapFragmentButtonRun.setText("Run");
    }

    //LOCATION UPDATES FUNCTIONS ------------------------
    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        //Location request configuration
        locationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(MAX_WAIT_TIME);

        //Request updates
        client.requestLocationUpdates(locationRequest, new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        },null);
    }

    public void onLocationChanged(Location lastLocation){
        String message = "Updated location ";
        LatLng latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        ///mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(latLng).title("New location"));

        locationArrayList.add(latLng);

        Polyline line = mMap.addPolyline(new PolylineOptions().width(5).color(Color.BLUE));
        line.setPoints(locationArrayList);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}