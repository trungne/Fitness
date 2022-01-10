package com.main.fitness.data.Model;

import java.util.List;

public class WorkoutSchedule {
    private final WorkoutSession[] schedule;
    private int currentSession;
    private final String workoutProgramName;
    public WorkoutSchedule(String workoutProgramName, WorkoutSession[] schedule, int currentSession){
        this.workoutProgramName = workoutProgramName;
        this.schedule = schedule;
        this.currentSession = currentSession;
    }

    public String getWorkoutProgramName() {
        return workoutProgramName;
    }

    public WorkoutSession[] getSchedule() {
        return schedule;
    }

    public WorkoutSession getCurrentSession(){
        return this.schedule[this.currentSession];
    }

    public WorkoutSession nextSession(){
        // loop back at the beginning when the current session is the last session
        if (this.currentSession == schedule.length - 1){
            this.currentSession = 0;
        }
        else{
            this.currentSession++;
        }

        return getCurrentSession();
    }

    public WorkoutSession previousSession(){
        // go to the back when the current session is the first session
        if(this.currentSession == 0){
            this.currentSession = schedule.length - 1;
        }
        else{
            this.currentSession--;
        }

        return getCurrentSession();
    }
}