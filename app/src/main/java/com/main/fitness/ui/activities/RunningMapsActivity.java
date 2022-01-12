package com.main.fitness.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.main.fitness.R;
import com.main.fitness.data.ViewModel.RunningViewModel;
import com.main.fitness.service.MyLocationUpdateService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class RunningMapsActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String TAG = "RunningMapsActivity";
    private GoogleMap mMap;
    protected FusedLocationProviderClient client;
    private RunningViewModel runningViewModel;
    private Intent backgroundUpdatesIntent;


    private TextView distanceTextView, stepTextView;
    private Button runButton;
    private FloatingActionButton currentLocationButton;


    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_maps);

        this.backgroundUpdatesIntent = new Intent(this, MyLocationUpdateService.class);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // init views
        this.runButton = findViewById(R.id.RunningActivityButton);
        this.runButton.setOnClickListener(v -> {
            startRunning();
        });

        this.distanceTextView = findViewById(R.id.RunningActivityDistanceValue);
        this.stepTextView = findViewById(R.id.RunningActivityStepValue);

        this.currentLocationButton = findViewById(R.id.RunningActivityMyLocatioButton);
        this.currentLocationButton.setOnClickListener(v -> getLocation());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.RunningMapsActivityMaps);
        if (mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        this.runningViewModel = new ViewModelProvider(this).get(RunningViewModel.class);
        this.runningViewModel.getLocationListLiveData().observe(this, locations -> {
            if (locations.size() < 2){
                return;
            }
            int size = locations.size();
            Location prev = locations.get(size - 2);
            Location current = locations.get(size - 1);

            updateRunningPolyline(prev, current);
        });

        this.runningViewModel.getStepsLiveData().observe(this, integer -> {
            this.stepTextView.setText(String.valueOf(integer));
        });
        this.runningViewModel.getDistanceLiveData().observe(this, aFloat -> {
            this.distanceTextView.setText(String.valueOf(aFloat));
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mMap is not initialized when the activity is first created
        if (this.mMap != null){
            this.mMap.clear();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        redraw();

    }

    private void startRunning(){
        // disable current location button
        this.currentLocationButton.setEnabled(false);
        EventBus.getDefault().register(this);
        startForegroundService(backgroundUpdatesIntent);

        startSensor();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.i(TAG, "MAP READY!!!!!!!!!!!!!!");
        this.mMap = googleMap;
        this.client = LocationServices.getFusedLocationProviderClient(this);
        this.mMap.getUiSettings().setZoomControlsEnabled(true);
        redraw();
        getLocation();
    }

    private void redraw(){
        if (this.mMap == null){
            Log.e(TAG, "Map not ready!");
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

        if (isLocationEnabled()) {
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
                this.mMap.addMarker(
                        new MarkerOptions()
                                .icon(bitMapDescriptorFromVector(this, R.drawable.my_location))
                                .position(latLng));
                this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        }
        else{
            // handle location not enabled
            Toast.makeText(this, "Please turn on location to use this service!", Toast.LENGTH_LONG).show();
            requestTurnOnLocation();
        }
    }


    //Function to check if the GPS Location is enabled in the settings
    public boolean isLocationEnabled(){
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        //If both boolean is returned with false
        if(!gpsEnabled && !networkEnabled) {
            // notify user
            requestTurnOnLocation();
            return false;
        }

        return true;
    }

    private void requestTurnOnLocation(){
        new MaterialAlertDialogBuilder(this)
                .setMessage("Location is currently off")
                .setPositiveButton("Open Location Settings", (dialog, which) -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.dismiss();
                })
                .setNeutralButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
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
        //open the map:
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
        Sensor stepDetectorSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetectorSensor != null){
            Toast.makeText(this, "You device supports step detector sensor", Toast.LENGTH_SHORT).show();
            this.sensorManager.registerListener(this.runningViewModel.getSensorEventListener(), stepDetectorSensor, 10);
        }
        else{
            Toast.makeText(this, "You device does not step detector sensor", Toast.LENGTH_SHORT).show();
            this.stepTextView.setText("No step detector found!");
        }
    }

    private void stopSensor(){
        Sensor stepDetectorSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetectorSensor != null){
            this.sensorManager.unregisterListener(this.runningViewModel.getSensorEventListener(), stepDetectorSensor);
        }
    }


}