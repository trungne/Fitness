package com.main.fitness.data.Model;

import java.util.List;

public class WorkoutSchedule {
    private final WorkoutSession[][] schedule;
    private int currentWeek;
    private int currentDay;
    private final String workoutProgramName;
    public WorkoutSchedule(String workoutProgramName, WorkoutSession[][] schedule, int week, int day){
        this.workoutProgramName = workoutProgramName;
        this.schedule = schedule;
        this.currentWeek = week;
        this.currentDay = day;
    }

    public String getWorkoutProgramName() {
        return workoutProgramName;
    }

    public WorkoutSession[][] getSchedule() {
        return schedule;
    }

    public WorkoutSession getCurrentSession(){
        return this.schedule[currentWeek][currentDay];
    }
}
