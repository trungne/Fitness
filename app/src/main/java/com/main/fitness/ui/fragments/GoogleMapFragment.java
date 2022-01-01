package com.main.fitness.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.main.fitness.R;
import com.main.fitness.ui.activities.MainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GoogleMapFragment extends Fragment implements SensorEventListener, LocationListener {

    //Fields for location updates
    private static final int UPDATE_INTERVAL = 10*1000; // 10 seconds
    private static final int FASTEST_INTERVAL = 2*1000; // 2 seconds
    private static final int MAX_WAIT_TIME = 1000;
    private static final int my_request_permission_code = 99;
    private final static int SAVE_OFFSET_STEPS = 500;
    private static int steps = 0;
    private boolean allowUpdates = false;
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
    private double mapFragmentUserTravelledDistance;
    private Location prev;
    private Location current;


    //xml
    private TextView mapFragmentSelectedDistanceTextView;
    private TextView mapFragmentTravelledDistanceTextView;
    private LinearLayout innerLinearLayoutMap;
    private Button mapFragmentButtonRun;
    private Button mapFragmentButtonStop;
    private Button mapFragmentButtonGetCurrentLocation;




    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            //Location monitor
            client = LocationServices.getFusedLocationProviderClient(requireActivity());
            mMap = googleMap;
            sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
            sensor= sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

            //Add function to zoom in and out on the map
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //Get current location
            getLocation();

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
        mapFragmentTravelledDistanceTextView = v.findViewById(R.id.map_fragment_travelled_distance);
        innerLinearLayoutMap = v.findViewById(R.id.map_inner_linear_layout);
        mapFragmentButtonRun = v.findViewById(R.id.map_fragment_button_run);
        mapFragmentButtonStop = v.findViewById(R.id.map_fragment_button_stop_run);
        mapFragmentButtonGetCurrentLocation = v.findViewById(R.id.map_fragment_button_current_location);


        //Actions for the buttons
        mapFragmentButtonRun.setOnClickListener(this::startRunningButton);
        mapFragmentButtonStop.setOnClickListener(this::stopRunningButton);
        mapFragmentButtonGetCurrentLocation.setOnClickListener(v1 -> getLocation());

        //Set data and set layout for linear layout
        try{
            //Data for textview
            assert bundle != null;
            data = bundle.getString("selectedRunningDistance");
            mapFragmentSelectedDistanceTextView.setText(data);
            //Because we just start running, we need to set the value as 0
            mapFragmentUserTravelledDistance = 0;
            mapFragmentTravelledDistanceTextView.setText("" + mapFragmentUserTravelledDistance);

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
            steps++;
            updateIfNecessary();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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
        }).addOnFailureListener(e -> Toast.makeText(requireActivity(), "Custom Logo Something Wrong ! FAILURE LISTENER ", Toast.LENGTH_LONG).show());
    }


    //Function to update the steps on the system
    private void updateIfNecessary() {
        if (steps > lastSaveSteps + SAVE_OFFSET_STEPS) {
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

    //Show Stop dialog when user pressed the STOP button on the map screen
    private void showStopDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Steps: " + steps + "\nDo you want to stop running?")
                .setNegativeButton("Keep Running", (dialog, id) -> dialog.cancel())
                .setPositiveButton("Accept", (dialog, which) -> {
                    try{
                        //Stop updating user location
                        stopLocationUpdates();

                        //Process the removal
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        //Remove this fragment
                        fragmentTransaction.remove(GoogleMapFragment.this);
                        fragmentTransaction.commit();
                        fragmentManager.popBackStack();
                    }
                    //Catch null value
                    catch (NullPointerException e){
                        Toast.makeText(requireActivity(), "Null Pointer Exception occurred !", Toast.LENGTH_SHORT).show();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //Start running button
    @SuppressLint("SetTextI18n")
    private void startRunningButton(View v){
        //Change the zoom in a-bit for closer look on the street
        mapFragmentButtonRun.setEnabled(false);
        mapFragmentButtonRun.setText("Running");

        //Start updating location
        startLocationUpdate();
    }

    @SuppressLint("SetTextI18n")
    private void stopRunningButton(View v) {
        showStopDialog();
    }

    //LOCATION UPDATES FUNCTIONS ------------------------
    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        //Allow the updates
        allowUpdates = true;

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

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onLocationChanged(Location lastLocation){

        //Hey, a non null location! Sweet!

        //open the map:
        LatLng latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());

        //Update location
        prev = current;
        current = lastLocation;

        //Calculate distance
        mapFragmentUserTravelledDistance += ((prev.distanceTo(current)) / 1000);
        //Format them into shorter string, to save space
        DecimalFormat df = new DecimalFormat("#0.000");
        mapFragmentTravelledDistanceTextView.setText("" + df.format(mapFragmentUserTravelledDistance));


        //Draw the polyline
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(prev.getLatitude(),prev.getLongitude()), new LatLng(current.getLatitude(),current.getLongitude()))
                .width(5)
                .color(Color.RED));

        //Move the camera to the new location point
        if(getActivity() != null){
            Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

    }

    //Function to force portrait mode on this fragment, onResume and onPause
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }

    }

    //Stop updating user location on interval
    public void stopLocationUpdates(){
        client.removeLocationUpdates(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                allowUpdates = false;
                if(getActivity() != null){
                    Toast.makeText(requireActivity(), "stopLocationUpdates function is invoked !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isLocationEnabled(Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            Toast.makeText(requireActivity(), "Location is currently off :(", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(context)
                    .setMessage("Location is currently off")
                    .setPositiveButton("Open Location Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .show();
            return false;
        }

        Toast.makeText(requireActivity(), "Location is enabled :)", Toast.LENGTH_SHORT).show();
        return true;
    }

    @SuppressLint("MissingPermission")
    public void getLocation(){
        if (isLocationEnabled(requireActivity())) {
            LocationManager locationManager = (LocationManager)  requireActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude,longitude);

                //Update the previous and current location global variables, IMPORTANT !!!
                prev = location;
                current = location;

                //Adjust the camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                Toast.makeText(requireActivity(), "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();

            }
            else{
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider,1000,0,this::onLocationChanged);
            }
        }
        else
        {
            //prompt user to enable location....
            //.................
        }
    }





}