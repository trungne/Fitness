package com.main.fitness.data.ViewModel;

import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.List;

public class RunningViewModel extends AndroidViewModel {
    private static final String TAG = "LocationViewModel";
    private Application mApplication;
    private MutableLiveData<List<Location>> locationListLiveData;
    private List<Location> locationList;

    private MutableLiveData<Integer> stepsLiveData;
    private MutableLiveData<Float> distanceLiveData;

    private SensorEventListener sensorEventListener;

    private MutableLiveData<Boolean> isRunningLiveData;

    private String startTime;
    // end time will be decided later by the application


    public RunningViewModel(@NonNull Application application) {
        super(application);
        this.mApplication = application;

        this.isRunningLiveData = new MutableLiveData<>(false);

        // distance live data
        this.distanceLiveData = new MutableLiveData<>(0f);

        // location live data
        this.locationList = new LinkedList<>();
        this.locationListLiveData = new MutableLiveData<>();
        this.locationListLiveData.setValue(this.locationList);

        // step live data
        this.stepsLiveData = new MutableLiveData<>(0);
        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Integer currentStep = stepsLiveData.getValue();
                if (currentStep != null){
                    stepsLiveData.setValue(currentStep + 1);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }


    public void setRunning(boolean isRunning){
        this.isRunningLiveData.setValue(isRunning);
    }

    public void setStartTime(String time){
        this.startTime = time;
    }

    public String getStartTime() {
        return startTime;
    }

    public MutableLiveData<Boolean> isRunningLiveData(){
        return this.isRunningLiveData;
    }

    public MutableLiveData<Float> getDistanceLiveData() {
        return distanceLiveData;
    }

    public SensorEventListener getSensorEventListener() {
        return sensorEventListener;
    }

    public MutableLiveData<Integer> getStepsLiveData() {
        return stepsLiveData;
    }

    public MutableLiveData<List<Location>> getLocationListLiveData() {
        if (locationListLiveData == null){
            this.locationList = new LinkedList<>();
            this.locationListLiveData = new MutableLiveData<>();
            this.locationListLiveData.setValue(this.locationList);
            return this.locationListLiveData;
        }
        return this.locationListLiveData;
    }

    private static final float MIN_UPDATE_DISTANCE = 1f;

    public void updateLocation(Location newLocation){
        int size = this.locationList.size();
        if (size >= 2){
            if(newLocation.distanceTo(this.locationList.get(size - 1)) < MIN_UPDATE_DISTANCE){
                return;
            }
        }

        if (size > 2){
            float distance = this.locationList.get(size - 1).distanceTo(newLocation);
            this.distanceLiveData.setValue(this.getDistanceLiveData().getValue() + distance);
        }

        this.locationList.add(newLocation);
        this.locationListLiveData.setValue(this.locationList);
    }

    @Override
    protected void onCleared() {
        Log.e(TAG, "onCleared called");
        super.onCleared();
    }
}
