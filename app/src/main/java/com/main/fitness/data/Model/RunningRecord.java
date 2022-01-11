package com.main.fitness.data.Model;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.ServerTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

public class RunningRecord {
    private String uid;
    private String time;
    private int step;


    private Duration duration; // do not use custom data type
    private String finishTime;
    private Double travelledDistance;

    private @ServerTimestamp Date mTimestamp;

    public RunningRecord(){
        // required by firebase
    }

    public RunningRecord(String uid, String time, int step, Duration duration, String finishTime, Double travelledDistance) {
        this.uid = uid;
        this.time = time;
        this.step = step;
        this.duration = duration;
        this.finishTime = finishTime;
        this.travelledDistance = travelledDistance;
    }
    public String getUid() {
        return uid;
    }

    public Double getTravelledDistance() {
        return travelledDistance;
    }

    public String getTime() {
        return time;
    }

    public int getStep() {
        return step;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getFinishTime() {
        return finishTime;
    }

    @ServerTimestamp
    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(@Nullable Date mTimestamp) {
        this.mTimestamp = mTimestamp;
    }
}
