package com.main.fitness.data.Model;

import java.util.List;

public class WorkoutSchedule {
    private final WorkoutSession[][] schedule;
    private final String workoutProgramName;
    public WorkoutSchedule(String workoutProgramName, WorkoutSession[][] schedule){
        this.workoutProgramName = workoutProgramName;
        this.schedule = schedule;
    }

    public String getWorkoutProgramName() {
        return workoutProgramName;
    }

    public WorkoutSession[][] getSchedule() {
        return schedule;
    }

    public WorkoutSession getWorkoutSession(int week, int day){
        return this.schedule[week][day];
    }
}
