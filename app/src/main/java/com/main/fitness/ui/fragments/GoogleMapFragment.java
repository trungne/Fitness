package com.main.fitness.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.main.fitness.R;
import com.main.fitness.data.Model.RunningRecord;
import com.main.fitness.data.ViewModel.WorkoutRecordViewModel;
import com.main.fitness.service.LocationUpdateService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class GoogleMapFragment extends Fragment implements LocationListener {

    //Fields for location updates
    private static final int UPDATE_INTERVAL = 10*1000; // 10 seconds
    private static final int FASTEST_INTERVAL = 2*1000; // 2 seconds
    private static final int MAX_WAIT_TIME = 1000;
    private Integer steps;

    //Used for location operations
    protected FusedLocationProviderClient client;
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private Integer buttonCounter = 0;

    //For step counter running features
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private boolean stepDetectorSensorIsActivated;

    //Container for markers
    private double mapFragmentUserTravelledDistance;
    private Location prev;
    private Location current;
    private String data;

    //Time
    DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");
    private static LocalDateTime startTime = null;
    private static LocalDateTime endTime = null;
    private static Instant start = null;
    private static Instant end = null;


    //xml
    private TextView mapFragmentSelectedDistanceTextView;
    private TextView mapFragmentTravelledDistanceTextView;
    private LinearLayout innerLinearLayoutMap;
    private Button mapFragmentButtonRun;
    private Button mapFragmentButtonGetCurrentLocation;
    private BottomNavigationView navBar;


    //For running in the background
    Intent backgroundUpdatesIntent;
    Boolean userOnRunningState = false;
    ArrayList<LatLng> locationList;



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

            //Set the running status as false;
            buttonCounter = 0;
        }
    };

    private WorkoutRecordViewModel workoutRecordViewModel;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_google_map,container,false);
        this.workoutRecordViewModel = new ViewModelProvider(requireActivity()).get(WorkoutRecordViewModel.class);

        Bundle bundle = this.getArguments();

        //Load the layout, textview, linearlayout, buttons
        mapFragmentSelectedDistanceTextView = v.findViewById(R.id.map_fragment_selected_distance);
        mapFragmentTravelledDistanceTextView = v.findViewById(R.id.map_fragment_travelled_distance);
        innerLinearLayoutMap = v.findViewById(R.id.map_inner_linear_layout);
        mapFragmentButtonRun = v.findViewById(R.id.map_fragment_button_run);
        mapFragmentButtonGetCurrentLocation = v.findViewById(R.id.map_fragment_button_current_location);
        navBar = v.findViewById(R.id.MainActivityBottomNavigationView);

        //Actions for the buttons
        mapFragmentButtonRun.setOnClickListener(this::startRunningButton);
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
            mapFragmentButtonRun.setText("Click to run");
            mapFragmentButtonRun.setBackgroundColor(Color.GREEN);

            //Because we have not run yet, so we set the run boolean as false, steps counter is also 0
            stepDetectorSensorIsActivated = false;
            steps = 0;

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
    @SuppressLint("SetTextI18n")
    private void showStopDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Steps: " + steps + "\nDo you want to stop running?").setNegativeButton("Keep Running", (dialog, which) -> {
            navBar = requireActivity().findViewById(R.id.MainActivityBottomNavigationView);
            navBar.setVisibility(View.GONE);
            dialog.cancel();
        })
                .setPositiveButton("Accept", (dialog, which) -> {
                    try{


                        //Stop updating user location
                        if(getActivity() != null){
                            EventBus.getDefault().unregister(this);
                            getActivity().stopService(backgroundUpdatesIntent);
                        }

                        //Set the status of the button again
                        mapFragmentButtonGetCurrentLocation.setEnabled(true);
                        mapFragmentButtonRun.setText("Click to run");
                        mapFragmentButtonRun.setBackgroundColor(Color.GREEN);

                        //Remove the Step Detector listener and reset the counter to 0
                        steps = 0;
                        //We only unregister this listener when the sensor has been activated
                        if(stepDetectorSensorIsActivated){
                            sensorManager.unregisterListener(stepDetectorSensorEventListener,stepDetectorSensor);
                            //Turn off the boolean
                            stepDetectorSensorIsActivated = false;
                        }

                        // TODO 1: Create a Running Record object
                        endTime = LocalDateTime.now();
                        String finishTime = time.format(endTime);
                        String initialTime  = time.format(startTime);
                        end = Instant.now();
                        Duration duration = Duration.between(start, end);
                        int totalDistance = Integer.parseInt(data);
                        double travelledDistance = mapFragmentUserTravelledDistance;
                        boolean isTrackCompleted = false;
                        if (travelledDistance >= totalDistance) {
                            isTrackCompleted = true;
                        }

                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        String uid = firebaseAuth.getCurrentUser().getUid();

                        RunningRecord runningRecord = new RunningRecord(uid,initialTime,steps,duration,finishTime,totalDistance,isTrackCompleted,travelledDistance);


                        // upload running record to Firebase
                        this.workoutRecordViewModel.updateRunningRecord(runningRecord);

                        //Switch to the Travelled Fragment to display the final result
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        //Add data to the bundle for the map fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("userTravelledDistance",String.valueOf(mapFragmentUserTravelledDistance));
                        bundle.putString("userTotalDistance",String.valueOf(totalDistance));
                        bundle.putString("userTrackCompletedStatus",String.valueOf(isTrackCompleted));
                        bundle.putString("userInitialTime",initialTime);
                        bundle.putString("userFinishedTime",finishTime);
                        bundle.putString("userSteps",String.valueOf(steps));
                        bundle.putString("userDuration",String.valueOf(duration.toMinutes()));

                        NotifyCompletedRunFragment notifyCompletedRunFragment = new NotifyCompletedRunFragment();
                        notifyCompletedRunFragment.setArguments(bundle);

                        //Switch to Map Fragment
                        fragmentTransaction.remove(GoogleMapFragment.this);
                        fragmentTransaction.add(R.id.MainActivityFragmentContainer,notifyCompletedRunFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

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

        //Initialize the location list
        locationList = new ArrayList<>();


        //Condition
        if(mapFragmentButtonRun.getText().toString().equals("Click to run")){
            //Change the zoom in a-bit for closer look on the street
            mapFragmentButtonGetCurrentLocation.setEnabled(false);


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

            //Initial time
            startTime = LocalDateTime.now();
            start = Instant.now();

            //Start updating location
            if(getActivity() != null){
                EventBus.getDefault().register(this);
                backgroundUpdatesIntent = new Intent(getActivity(), LocationUpdateService.class);
                getActivity().startForegroundService(backgroundUpdatesIntent);
            }


            navBar = requireActivity().findViewById(R.id.MainActivityBottomNavigationView);
            navBar.setVisibility(View.GONE);

            //Change the color of the button
            mapFragmentButtonRun.setBackgroundColor(Color.RED);
            mapFragmentButtonRun.setText("STOP");
            userOnRunningState = true;
        }

        else if(mapFragmentButtonRun.getText().toString().equals("STOP")){
            navBar = requireActivity().findViewById(R.id.MainActivityBottomNavigationView);
            navBar.setVisibility(View.VISIBLE);
            showStopDialog();
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

                Toast.makeText(requireActivity(), "Prev -> " + "lat: " + prev.getLatitude() + "long: "  + prev.getLongitude(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(requireActivity(), "Current ->" + "lat: " + current.getLatitude() + " long :" + current.getLongitude(), Toast.LENGTH_SHORT).show();
                //Adjust the camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(new MarkerOptions().icon(bitMapDescriptorFromVector(requireActivity(), R.drawable.my_location))
                        .position(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                //Toast.makeText(requireActivity(), "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();

            }
            else {
                //Request location updates then move the camera
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);

                //Move the camera
                try {
                    double latitude = Objects.requireNonNull(location).getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);

                    //Update the previous and current location global variables, IMPORTANT !!!
                    prev = location;
                    current = location;

                    //Adjust the camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.addMarker(new MarkerOptions().icon(bitMapDescriptorFromVector(requireActivity(), R.drawable.my_location))
                            .position(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                }

                catch(NullPointerException e){
                    e.printStackTrace();
                }

            }
        }
    }

    //Help to add new image for icon
    private BitmapDescriptor bitMapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0,
                vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    //***STEP DETECTOR EVENT LISTENER***//
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


    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Location lastLocation) {
        //open the map:
        LatLng latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());

        //Update location
        prev = current;
        current = lastLocation;

        //Calculate
        if (prev != null) {
            mapFragmentUserTravelledDistance += ((prev.distanceTo(current)) / 1000);
            //Format them into shorter string, to save space
            DecimalFormat df = new DecimalFormat("#0.000");
            mapFragmentTravelledDistanceTextView.setText("" + df.format(mapFragmentUserTravelledDistance));


            //Draw the polyline
            mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(prev.getLatitude(), prev.getLongitude()), new LatLng(current.getLatitude(), current.getLongitude()))
                    .width(10)
                    .color(Color.RED));
        }
        //Move the camera to the new location point, this line put here without an if get activity != null is to check whether the
        //onLocationChanged method is still being invoked after user chose to stop running
        //Toast.makeText(requireActivity(), "Updated", Toast.LENGTH_SHORT).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}