package com.main.fitness.data.Model;

import java.time.Duration;
import java.time.LocalDateTime;

public class RunningRecord {
    private String name;
    private String time;
    private int step;
    private Duration duration;
    private String finishTime;

    public RunningRecord(String name, String time, int step, Duration duration, String finishTime) {
        this.name = name;
        this.time = time;
        this.step = step;
        this.finishTime = finishTime;
    }

    public String getName() {
        return name;
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
