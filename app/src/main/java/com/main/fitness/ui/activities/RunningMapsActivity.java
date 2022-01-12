package com.main.fitness.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.main.fitness.R;
import com.main.fitness.data.Model.RunningRecord;
import com.main.fitness.data.ViewModel.RunningViewModel;
import com.main.fitness.data.ViewModel.WorkoutRecordViewModel;
import com.main.fitness.service.MyLocationUpdateService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.LocalDateTime;
import java.util.List;

public class RunningMapsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback  {
    private static final String TAG = "RunningMapsActivity";
    private static final int PERMISSION_REQUEST_SENSOR = 99;
    private static final int PERMISSION_REQUEST_LOCATION = 49;

    private RunningViewModel runningViewModel;

    private GoogleMap mMap;
    protected FusedLocationProviderClient client;
    private Intent backgroundUpdatesIntent;

    private TextView distanceTextView, stepTextView, stepLabel;
    private Button runButton;
    private FloatingActionButton currentLocationButton;

    private SensorManager sensorManager;
    private Marker mMarker;
    
    // callback to handle request permission for sensor and location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SENSOR) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start sensor.
                startSensor();
            } else {
                // Permission request was denied.
                stepLabel.setVisibility(View.GONE);
                stepTextView.setVisibility(View.GONE);
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSION_REQUEST_LOCATION){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start sensor.
                getLocation();
            } else {
                // Permission request was denied.
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Location required!")
                        .setMessage("You must enable location to use this service.")
                        .setPositiveButton("Turn on location", (dialog, which) -> {
                            getLocation();
                            dialog.dismiss();
                        })
                        .setNeutralButton("Go Back", (dialog, which) -> {
                            finish();
                            dialog.dismiss();
                        })
                        .show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_maps);

        this.backgroundUpdatesIntent = new Intent(this, MyLocationUpdateService.class);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // init views
        this.runButton = findViewById(R.id.RunningActivityButton);


        this.distanceTextView = findViewById(R.id.RunningActivityDistanceValue);

        this.stepTextView = findViewById(R.id.RunningActivityStepValue);
        this.stepLabel = findViewById(R.id.RunningActitivtyStepLabel);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            stepLabel.setVisibility(View.GONE);
            stepTextView.setVisibility(View.GONE);
        }

        this.currentLocationButton = findViewById(R.id.RunningActivityMyLocatioButton);
        this.currentLocationButton.setOnClickListener(v -> getLocation());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.RunningMapsActivityMaps);
        if (mapFragment != null){
            mapFragment.getMapAsync(this);
        }



        this.runningViewModel = new ViewModelProvider(this).get(RunningViewModel.class);

        this.runningViewModel.isRunningLiveData().observe(this, isRunning -> {
            if (isRunning){
                this.runButton.setText("Stop");
                this.runButton.setOnClickListener(v -> {
                    askForStopConfirmation();
                });
            }
            else{
                this.runButton.setText("Run");
                this.runButton.setOnClickListener(v -> {
                    startRunning();
                });
            }
        });


        this.runningViewModel.getLocationListLiveData().observe(this, locations -> {
            Log.e(TAG, "getLocationListLiveData Listener called");
            if (locations.size() < 2){
                return;
            }
            int size = locations.size();
            Location prev = locations.get(size - 2);
            Location current = locations.get(size - 1);

            updateRunningPolyline(prev, current);
            redrawCurrentLocationMarker(new LatLng(current.getLatitude(), current.getLongitude()));
        });

        this.runningViewModel.getStepsLiveData().observe(this, integer -> {
            this.stepTextView.setText(String.valueOf(integer));
        });
        this.runningViewModel.getDistanceLiveData().observe(this, aFloat -> {
            this.distanceTextView.setText(String.valueOf(aFloat));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        redrawPolyline();
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cancel Running?")
                .setMessage("You haven't finished the session. Do you want to cancel it?")
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> {
                    stopRunning();
                    dialog.dismiss();
                    super.onBackPressed();
                })
                .show();
    }

    private void askForStopConfirmation(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Finish Running?")
                .setMessage("Do you want to finish this session?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    stopRunning();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void stopRunning(){
        stopSensor();
        this.currentLocationButton.setEnabled(true);
        this.runningViewModel.setRunning(false);

        EventBus.getDefault().unregister(this);
        stopService(this.backgroundUpdatesIntent);


        String uid = FirebaseAuth.getInstance().getUid();
        if (TextUtils.isEmpty(uid)){
            return;
        }

        String finishTime = LocalDateTime.now().toString();
        String startTime = this.runningViewModel.getStartTime();
        Float distance = this.runningViewModel.getDistanceLiveData().getValue();
        Integer steps = this.runningViewModel.getStepsLiveData().getValue();

        Intent saveRecordIntent = new Intent(this, ShowRunningRecordActivity.class);
        saveRecordIntent.putExtra(ShowRunningRecordActivity.START_TIME_KEY, startTime);
        saveRecordIntent.putExtra(ShowRunningRecordActivity.FINISH_TIME_KEY, finishTime);
        saveRecordIntent.putExtra(ShowRunningRecordActivity.DISTANCE_KEY, distance);
        saveRecordIntent.putExtra(ShowRunningRecordActivity.STEPS_KEY, steps);
        startActivity(saveRecordIntent);

        this.runningViewModel.clearRunningData();
    }

    private boolean checkSensorPermission(){
        //Check for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            //If permission is not accepted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                //Request permission
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACTIVITY_RECOGNITION
                }, PERMISSION_REQUEST_SENSOR);
            } else {
                return true;
            }
        }


        return false;
    }

    private boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSION_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }


    private void startRunning(){
        // disable current location button
        this.currentLocationButton.setEnabled(false);
        this.runningViewModel.setRunning(true);
        this.runningViewModel.setStartTime(LocalDateTime.now().toString());

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        startForegroundService(backgroundUpdatesIntent);
        startSensor();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;
        this.client = LocationServices.getFusedLocationProviderClient(this);
        this.mMap.getUiSettings().setZoomControlsEnabled(true);
        redrawPolyline();
        getLocation();
    }

    private void redrawPolyline(){
        if (this.mMap == null){
            return;
        }

        List<Location> locations = this.runningViewModel.getLocationListLiveData().getValue();

        // redraw
        if (locations == null){
            return;
        }

        for (int i = 0 ; i < locations.size() - 1; i++){
            Location prev = locations.get(i);
            Location current = locations.get(i + 1);
            updateRunningPolyline(prev, current);
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation(){

        if (checkLocationPermission()) {
            LocationManager locationManager = (LocationManager)  getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude,longitude);

                //Adjust the camera
                this.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                redrawCurrentLocationMarker(latLng);
            }
        }
        else{
            // handle location not enabled
            Toast.makeText(this, "Please turn on location to use this service!", Toast.LENGTH_LONG).show();
        }
    }

    private void redrawCurrentLocationMarker(LatLng latLng){
        if (this.mMap == null){
            return;
        }
        if (this.mMarker != null){
            this.mMarker.remove();
        }

        this.mMarker = this.mMap.addMarker(new MarkerOptions()
                .icon(bitMapDescriptorFromVector(this, R.drawable.my_location))
                .position(latLng));
    }


    //Help to add new image for icon
    private static BitmapDescriptor bitMapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        // check null
        vectorDrawable.setBounds(0, 0,
                vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Location lastLocation) {
        this.runningViewModel.updateLocation(lastLocation);
    }

    private void updateRunningPolyline(Location prev, Location current){
        //Draw the polyline
        if (this.mMap == null){
            return;
        }

        Polyline p = this.mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(prev.getLatitude(), prev.getLongitude()), new LatLng(current.getLatitude(), current.getLongitude()))
                .width(10)
                .color(Color.RED));

        //Move the camera to the new location point, this line put here without an if get activity != null is to check whether the
        //onLocationChanged method is still being invoked after user chose to stop running
        //Toast.makeText(requireActivity(), "Updated", Toast.LENGTH_SHORT).show();
        LatLng latLng = new LatLng(current.getLatitude(), current.getLongitude());
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void startSensor(){
        if(checkSensorPermission()){
            Sensor stepDetectorSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (stepDetectorSensor != null){
                Toast.makeText(this, "Step detector sensor enabled!", Toast.LENGTH_SHORT).show();
                this.sensorManager.registerListener(this.runningViewModel.getSensorEventListener(), stepDetectorSensor, 10);
                stepLabel.setVisibility(View.VISIBLE);
                stepTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void stopSensor(){
        Sensor stepDetectorSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetectorSensor != null){
            this.sensorManager.unregisterListener(this.runningViewModel.getSensorEventListener(), stepDetectorSensor);
        }
    }
}