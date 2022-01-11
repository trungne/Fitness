package com.main.fitness.data.Model;

import java.time.Duration;
import java.time.LocalDateTime;

public class RunningRecord {
    private String uid;
    private String time;
    private int step;
    private Duration duration;
    private String finishTime;
    private Double travelledDistance;

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
}
