package com.main.fitness.ui.fragments;

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
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.main.fitness.R;

import java.text.DecimalFormat;

public class GoogleMapFragment extends Fragment implements LocationListener {

    //Fields for location updates
    private static final int UPDATE_INTERVAL = 10*1000; // 10 seconds
    private static final int FASTEST_INTERVAL = 2*1000; // 2 seconds
    private static final int MAX_WAIT_TIME = 1000;
    private static final int PARTIAL_WAKE_LOCK = 1;
    private Integer steps;
    private boolean allowUpdates = false;
    private PowerManager.WakeLock wakeLock;

    //Used for location operations
    protected FusedLocationProviderClient client;
    private GoogleMap mMap;
    private LocationRequest locationRequest;

    //For step counter running features
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private boolean stepDetectorSensorIsActivated;

    //Container for markers
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

            //Add function to zoom in and out on the map
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //Get current location
            getLocation();

        }
    };

    @SuppressLint("SetTextI18n")
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
        Context mContext = getContext();
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock =  powerManager.newWakeLock(PARTIAL_WAKE_LOCK,"motionDetection:keepAwake");
        wakeLock.acquire();



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

            //Because we have not run yet, so we set the run boolean as false
            stepDetectorSensorIsActivated = false;

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

    //Show Stop dialog when user pressed the STOP button on the map screen
    private void showStopDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Steps: " + steps + "\nDo you want to stop running?")
                .setNegativeButton("Keep Running", (dialog, id) -> dialog.cancel())
                .setPositiveButton("Accept", (dialog, which) -> {
                    try{

                        //Stop updating user location
                        stopLocationUpdates();

                        //Remove the Step Detector listener and reset the counter to 0
                        steps = 0;
                        //We only unregister this listener when the sensor has been activated
                        if(stepDetectorSensorIsActivated){
                            sensorManager.unregisterListener(stepDetectorSensorEventListener,stepDetectorSensor);
                            //Turn off the boolean
                            stepDetectorSensorIsActivated = false;
                        }

                        //Process the fragment removal
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        //Remove this fragment
                        fragmentTransaction.remove(GoogleMapFragment.this);
                        fragmentTransaction.commit();
                        fragmentManager.popBackStack();
                    }
                    //Catch null value
                    catch (NullPointerException e){
                        e.printStackTrace();
                        Toast.makeText(requireActivity(), "Null Pointer Exception occurred !", Toast.LENGTH_SHORT).show();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //Start running button, we will also initialize the step counter sensor
    @SuppressLint("SetTextI18n")
    private void startRunningButton(View v){
        //Change the zoom in a-bit for closer look on the street
        mapFragmentButtonRun.setEnabled(false);
        mapFragmentButtonRun.setText("Running");

        //initialize the step DETECTOR SENSOR and the STEP counter global variable
        steps = 0;
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE); //Can be Context.SENSOR_SERVICE or AppCombatActivity.SENSOR_SERVICE
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepDetectorSensor != null){
            sensorManager.registerListener(stepDetectorSensorEventListener,stepDetectorSensor,10);
            //sensorManager.requestTriggerSensor(stepDetectorTriggerEventListener,stepDetectorSensor);
            //Boolean is now true
            stepDetectorSensorIsActivated = true;
        }

        //Start updating location
        startLocationUpdate();
    }

    @SuppressLint("SetTextI18n")
    private void stopRunningButton(View v) {
        wakeLock.release();
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

    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n"})
    @Override
    public void onLocationChanged(Location lastLocation){

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
                .width(10)
                .color(Color.CYAN));

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

    //Function to check if the GPS Location is enabled in the settings
    public boolean isLocationEnabled(Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        //If both boolean is returned with false
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

    //Function to move the user camera to their current location
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
            else {
                //Request location updates then move the camera
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this::onLocationChanged);

                //Move the camera
                try {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);

                    //Update the previous and current location global variables, IMPORTANT !!!
                    prev = location;
                    current = location;

                    //Adjust the camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                }

                catch(NullPointerException e){
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Listener that handles sensor events for STEP DETECTOR
     */

    private final SensorEventListener stepDetectorSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Do work
            steps += 1;
            //Avoid fragment not attached to context
            Toast.makeText(requireContext(), "STEP DETECTOR INVOKED !", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


}