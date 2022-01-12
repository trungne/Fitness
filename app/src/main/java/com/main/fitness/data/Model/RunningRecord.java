package com.main.fitness.data.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.time.LocalDateTime;

public class RunningRecord {
    private String uid;
    private String startTime;
    private String endTime;
    private Float distance;
    private Integer steps;

    private @ServerTimestamp
    Timestamp timestamp;

    public RunningRecord(){

    }

    public RunningRecord(String uid, String startTime, String endTime, Float distance, Integer steps){
        this.uid = uid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.steps = steps;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getUid() {
        return uid;
    }

    @ServerTimestamp
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Float getDistance() {
        return distance;
    }

    public Integer getSteps() {
        return steps;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
