package com.main.fitness.data.Model;

import java.time.Duration;
import java.time.LocalDateTime;

public class RunningRecord {
    private String time;
    private int step;
    private Duration duration;
    private String finishTime;
    private int totalDistance;
    private boolean isTrackCompleted;
    private Double travelledDistance;

    public RunningRecord(int totalDistance, double travelledDistance, boolean isTrackCompleted,
                         String time, int step, Duration duration, String finishTime) {
        this.totalDistance = totalDistance;
        this.travelledDistance = travelledDistance;
        this.isTrackCompleted = isTrackCompleted;
        this.time = time;
        this.step = step;
        this.finishTime = finishTime;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public boolean isTrackCompleted() {
        return isTrackCompleted;
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
