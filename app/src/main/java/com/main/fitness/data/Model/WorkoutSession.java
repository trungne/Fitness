package com.main.fitness.data.Model;

import javax.annotation.Nullable;

public class WorkoutSession {
    private final WorkoutSet[] workoutSets;
    private int currentExerciseOrder;
    private final String[] targetMuscles;

    public WorkoutSession(String[] targetMuscles, WorkoutSet[] workoutSets){
        this.targetMuscles = targetMuscles;
        this.workoutSets = workoutSets;
        this.currentExerciseOrder = 0;
    }

    public String[] getTargetMuscles() {
        return targetMuscles;
    }

    @Nullable
    public WorkoutSet nextExercise(){
        if (this.currentExerciseOrder == workoutSets.length - 1){
            return null;
        }
        return this.workoutSets[++currentExerciseOrder];
    }

    public WorkoutSet previousExercise(){
        if (this.currentExerciseOrder == 0){
            return null;
        }
        return this.workoutSets[--currentExerciseOrder];
    }

    public WorkoutSet[] getExerciseSets() {
        return workoutSets;
    }

    public int getCurrentExerciseOrder(){
        return this.currentExerciseOrder;
    }

    public WorkoutSet getCurrentExercise(){
        return this.workoutSets[this.currentExerciseOrder];
    }


}
